package wxdgaming.boot2.starter.batis.sql;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.collection.ConvertCollection;
import wxdgaming.boot2.core.collection.SplitCollection;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.executor.AbstractEventRunnable;
import wxdgaming.boot2.core.executor.AbstractExecutorService;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.Tick;
import wxdgaming.boot2.starter.batis.DataBatch;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * sql 模型 批量 处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 20:43
 **/
@Slf4j
public abstract class SqlDataBatch extends DataBatch {

    protected final SqlDataHelper sqlDataHelper;
    protected final List<BatchQueue> queueList;
    protected final AbstractExecutorService batchExecutorService;

    public SqlDataBatch(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        int batchThreadSize = sqlDataHelper.getSqlConfig().getBatchThreadSize();
        this.queueList = new ArrayList<>(batchThreadSize);
        this.batchExecutorService = ExecutorFactory.createPlatform("Sql-Batch-" + sqlDataHelper.getDbName(), batchThreadSize, 20000, QueuePolicyConst.AbortPolicy);
        log.info("{} 数据库: {} 创建 {} 个 sql 批量线程", sqlDataHelper.getClass().getSimpleName(), sqlDataHelper.getDbName(), batchThreadSize);
        for (int i = 1; i <= batchThreadSize; i++) {
            String queueName = "Sql-Batch-" + sqlDataHelper.getDbName() + "-" + i;
            BatchQueue batchQueue = new BatchQueue(i, queueName, sqlDataHelper.getSqlConfig().getBatchSubmitSize());
            this.queueList.add(batchQueue);
            this.batchExecutorService.scheduleWithFixedDelay(batchQueue, 200, 200, TimeUnit.MILLISECONDS);
        }
    }

    @Override public void stop() {
        batchExecutorService.closeAndWait();
    }

    @SuppressWarnings("unchecked")
    public <SDH extends SqlDataHelper> SDH dataHelper() {
        return (SDH) sqlDataHelper;
    }

    @Override public void save(Entity entity) {
        Boolean newEntity = entity.getNewEntity();
        if (newEntity == null) {
            newEntity = !this.sqlDataHelper.existBean(entity);
        }
        if (newEntity)
            insert(entity);
        else
            update(entity);
        entity.setNewEntity(false);
    }

    BatchQueue batchQueue(Entity entity) {
        int hashCode = entity.hashCode();
        int index = Math.abs(hashCode) % queueList.size();
        return queueList.get(index);
    }

    @Override public void insert(Entity entity) {
        batchQueue(entity).insert(entity);
    }

    @Override public void update(Entity entity) {
        batchQueue(entity).update(entity);
    }

    public class BatchQueue extends AbstractEventRunnable {

        protected final ReentrantLock lock = new ReentrantLock();
        protected AtomicBoolean closed = new AtomicBoolean();
        protected final int threadId;
        protected final int batchSubmitSize;
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, ConvertCollection<BatchParam>> batchInsertMap = new Table<>();
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, ConvertCollection<BatchParam>> batchUpdateMap = new Table<>();

        protected long executeDiffTime = 0;
        protected long executeCount = 0;
        protected Tick ticket = new Tick(1, TimeUnit.MINUTES);

        public BatchQueue(int threadId, String name, int batchSubmitSize) {
            this.setQueueName(name);
            this.threadId = threadId;
            this.batchSubmitSize = batchSubmitSize;
        }

        @Override public long getExecutorWarnTime() {
            return 30 * 1000;
        }

        public void insert(Entity entity) {
            String tableName = TableMapping.beanTableName(entity);
            TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
            String insertSql = sqlDataHelper.ddlBuilder().buildInsertSql(tableMapping, tableName);

            BatchParam batchParam = null;

            for (int i = 0; i < 3; i++) {
                try {
                    Object[] keyParams = sqlDataHelper.ddlBuilder().buildKeyParams(tableMapping, entity);
                    Object[] insertParams = sqlDataHelper.ddlBuilder().buildInsertParams(tableMapping, entity);
                    batchParam = new BatchParam(entity, keyParams, insertParams);
                    break;
                } catch (ConcurrentModificationException ignore) {}
            }

            if (batchParam == null) {
                log.info("insert ConcurrentModificationException {}", entity.getClass());
                return;
            }

            lock.lock();
            try {
                ConvertCollection<BatchParam> collection = batchInsertMap.computeIfAbsent(tableName, insertSql, k -> new ConvertCollection<>());
                collection.add(batchParam);
                entity.setNewEntity(false);
            } finally {
                lock.unlock();
            }
        }

        public void update(Entity entity) {
            String tableName = TableMapping.beanTableName(entity);
            TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
            String updateSql = sqlDataHelper.ddlBuilder().buildUpdateSql(tableMapping, tableName);
            BatchParam batchParam = null;

            for (int i = 0; i < 3; i++) {
                try {
                    Object[] keyParams = sqlDataHelper.ddlBuilder().buildKeyParams(tableMapping, entity);
                    Object[] updateParams = sqlDataHelper.ddlBuilder().builderUpdateParams(tableMapping, entity);
                    batchParam = new BatchParam(entity, keyParams, updateParams);
                    break;
                } catch (ConcurrentModificationException ignore) {}
            }

            if (batchParam == null) {
                log.info("update ConcurrentModificationException {}", entity.getClass());
                return;
            }

            lock.lock();
            try {
                ConvertCollection<BatchParam> collection = batchUpdateMap.computeIfAbsent(tableName, updateSql, k -> new ConvertCollection<>());
                collection.add(batchParam);
            } finally {
                lock.unlock();
            }
        }

        @Override public void onEvent() throws Exception {
            insertBach();
            updateBach();
        }

        protected void insertBach() {
            Table<String, String, ConvertCollection<BatchParam>> tmp;
            lock.lock();
            try {
                if (batchInsertMap.isEmpty()) {
                    return;
                }
                tmp = batchInsertMap;
                batchInsertMap = new Table<>();
            } finally {
                lock.unlock();
            }
            executeBach("insertBach", tmp);
        }

        protected void updateBach() {
            Table<String, String, ConvertCollection<BatchParam>> tmp;
            lock.lock();
            try {
                if (batchUpdateMap.isEmpty()) {
                    return;
                }
                tmp = batchUpdateMap;
                batchUpdateMap = new Table<>();
            } finally {
                lock.unlock();
            }
            executeBach("updateBach", tmp);
        }

        protected void executeBach(String sqlType, Table<String, String, ConvertCollection<BatchParam>> tmp) {
            for (HashMap<String, ConvertCollection<BatchParam>> map : tmp.values()) {
                for (Map.Entry<String, ConvertCollection<BatchParam>> entry : map.entrySet()) {
                    String sql = entry.getKey();
                    ConvertCollection<BatchParam> values = entry.getValue();
                    SplitCollection<BatchParam> splitCollection = new SplitCollection<>(batchSubmitSize, values.getNodes());
                    while (!splitCollection.isEmpty()) {
                        List<BatchParam> batchParams = splitCollection.removeFirst();
                        long nanoTime = System.nanoTime();
                        int executeCount = executeUpdate(sqlType, sql, batchParams);
                        long diff = (System.nanoTime() - nanoTime) / 10000;
                        executeDiffTime += diff;
                        this.executeCount += executeCount;
                        if (sqlDataHelper.getSqlConfig().isDebug() || ticket.need() || closed.get() || SpringUtil.exiting.get()) {
                            log.info(
                                    """
                                            
                                            %s 数据库: %s, %s(%s) , 单次批量提交数量限制: %s, 当前待提交剩余: %s 条
                                            本次 count: %s 条 耗时: %s ms 性能：%s 条/s
                                            累计 count: %s 条 耗时: %s ms 性能：%s 条/s
                                            """
                                            .formatted(
                                                    sqlDataHelper.getClass().getSimpleName(),
                                                    sqlDataHelper.getDbName(),
                                                    sqlType, sql,
                                                    batchSubmitSize, splitCollection.size(),
                                                    StringUtils.leftPad(String.valueOf(executeCount), 19, ' '),
                                                    StringUtils.leftPad(String.valueOf(diff / 100f), 19, ' '),
                                                    StringUtils.leftPad(String.valueOf(executeCount / (diff / 100f) * 1000), 19, ' '),
                                                    StringUtils.leftPad(String.valueOf(this.executeCount), 19, ' '),
                                                    StringUtils.leftPad(String.valueOf(this.executeDiffTime / 100f), 19, ' '),
                                                    StringUtils.leftPad(String.valueOf(this.executeCount / (this.executeDiffTime / 100f) * 1000), 19, ' ')
                                            )
                            );
                        }
                    }
                }
            }
        }

        protected int executeUpdate(String sqlType, String sql, List<BatchParam> paramList) {
            int insertCount = 0;
            try (Connection connection = sqlDataHelper.connection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    for (BatchParam objects : paramList) {
                        for (int i = 0; i < objects.params.length; i++) {
                            Object object = objects.params[i];
                            preparedStatement.setObject(i + 1, object);
                        }
                        preparedStatement.addBatch();
                        insertCount++;
                    }
                    preparedStatement.executeBatch();
                    connection.commit();
                }
            } catch (Exception e) {
                insertCount = 0;
                for (BatchParam batchParam : paramList) {
                    try (Connection connection = sqlDataHelper.connection();
                         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        for (int i = 0; i < batchParam.params.length; i++) {
                            Object object = batchParam.params[i];
                            preparedStatement.setObject(i + 1, object);
                        }
                        preparedStatement.executeUpdate();
                        insertCount++;
                    } catch (Exception e1) {
                        if (getErrorCallback() != null) {
                            getErrorCallback().accept(sqlType, batchParam.entity);
                        } else {
                            File file = new File("target/db_error/" + sqlDataHelper.getDbName() + "/" + this.threadId + "_" + System.nanoTime() + ".txt");
                            String formatted = "db batch error\nsql: %s\ndata: %s\n\nerror: \n%s".formatted(sql, FastJsonUtil.toJSONString(batchParam), e1.toString());
                            FileWriteUtil.writeString(file, formatted, true);
                            log.error("{}", formatted, e1);
                        }
                    }
                }
            }
            return insertCount;
        }
    }

    @Getter
    protected static class BatchParam {

        protected final Entity entity;
        /** 主键的值 */
        protected final Object[] keyParams;
        protected final Object[] params;

        public BatchParam(Entity entity, Object[] keyParams, Object[] params) {
            this.entity = entity;
            this.keyParams = keyParams;
            this.params = params;
        }

        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            BatchParam that = (BatchParam) o;
            return getEntity().equals(that.getEntity());
        }

        @Override public int hashCode() {
            return getEntity().hashCode();
        }

    }

}
