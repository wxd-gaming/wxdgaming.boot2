package wxdgaming.boot2.starter.batis.sql;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.collection.SplitCollection;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.lang.Tick;
import wxdgaming.boot2.starter.batis.DataBatch;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * sql 模型 批量 处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-16 20:43
 **/
@Slf4j
public abstract class SqlDataBatch extends DataBatch {

    protected final SqlDataHelper<?> sqlDataHelper;
    protected final List<BatchThread> batchThreads = new ArrayList<>();

    public SqlDataBatch(SqlDataHelper<?> sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        for (int i = 1; i <= 1; i++) {
            batchThreads.add(new BatchThread(i, sqlDataHelper.getDbName() + "-" + i));
        }
    }

    public <SDH extends SqlDataHelper<?>> SDH dataHelper() {
        return (SDH) sqlDataHelper;
    }

    @Override public void save(Entity entity) {
        if (entity.isNewEntity())
            sqlDataHelper.insert(entity);
        else
            sqlDataHelper.update(entity);
        entity.setNewEntity(false);
    }

    @Override public void insert(Entity entity) {
        int hashCode = entity.hashCode();
        BatchThread batchThread = batchThreads.get(hashCode % batchThreads.size());
        batchThread.insert(entity);
    }

    @Override public void update(Entity entity) {
        int hashCode = entity.hashCode();
        BatchThread batchThread = batchThreads.get(hashCode % batchThreads.size());
        batchThread.update(entity);
    }

    public class BatchThread extends Thread {

        protected final ReentrantLock lock = new ReentrantLock();
        protected final int threadId;
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, SplitCollection<Object[]>> batchInsertMap = new Table<>();
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, SplitCollection<Object[]>> batchUpdateMap = new Table<>();

        protected DiffTime diffTime = new DiffTime();
        protected long executeDiffTime = 0;
        protected long executeCount = 0;
        protected Tick ticket = new Tick(1, TimeUnit.MINUTES);

        public BatchThread(int threadId, String name) {
            super(name);
            this.threadId = threadId;
            this.setPriority(MAX_PRIORITY);
            this.setDaemon(true);
            this.start();
        }

        public void insert(Entity entity) {
            lock.lock();
            try {
                String tableName = TableMapping.beanTableName(entity);
                TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
                String insertSql = sqlDataHelper.getDdlBuilder().buildInsert(tableMapping, tableName);
                Object[] insertParams = sqlDataHelper.getDdlBuilder().buildInsertParams(tableMapping, entity);
                SplitCollection<Object[]> entitySplitCollection = batchInsertMap.computeIfAbsent(tableName, insertSql, k -> new SplitCollection<>(500));
                entitySplitCollection.add(insertParams);
            } finally {
                lock.unlock();
            }
        }

        public void update(Entity entity) {
            lock.lock();
            try {
                String tableName = TableMapping.beanTableName(entity);
                TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
                String updateSql = sqlDataHelper.getDdlBuilder().buildUpdate(tableMapping, tableName);
                Object[] updateParams = sqlDataHelper.getDdlBuilder().builderUpdateParams(tableMapping, entity);
                SplitCollection<Object[]> entitySplitCollection = batchUpdateMap.computeIfAbsent(tableName, updateSql, k -> new SplitCollection<>(500));
                entitySplitCollection.add(updateParams);
            } finally {
                lock.unlock();
            }
        }

        @Override public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(200);
                    insertBach();
                    updateBach();
                } catch (Throwable throwable) {
                    log.error("sqlDataBatch error", throwable);
                }
            }

        }

        protected void insertBach() {
            Table<String, String, SplitCollection<Object[]>> tmp;
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
            executeBach(tmp);
        }

        protected void updateBach() {
            Table<String, String, SplitCollection<Object[]>> tmp;
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
            executeBach(tmp);
        }

        protected void executeBach(Table<String, String, SplitCollection<Object[]>> tmp) {
            int insertCount = 0;
            diffTime.reset();
            Collection<HashMap<String, SplitCollection<Object[]>>> values = tmp.values();
            for (HashMap<String, SplitCollection<Object[]>> map : values) {
                for (Map.Entry<String, SplitCollection<Object[]>> entry : map.entrySet()) {
                    String insertSql = entry.getKey();
                    LinkedList<List<Object[]>> es = entry.getValue().getEs();
                    for (List<Object[]> list : es) {
                        insertCount += executeUpdate(insertSql, list);
                    }
                }
            }
            long diff = diffTime.diff100();
            executeDiffTime += diff;
            executeCount += insertCount;
            if (sqlDataHelper.getSqlConfig().isDebug() || ticket.need()) {
                log.info(
                        """
                                
                                db: %s %s execute bach
                                本次 count: %s 条 耗时: %s ms 性能：%s 条/s
                                累计 count: %s 条 耗时: %s ms 性能：%s 条/s
                                """
                                .formatted(
                                        sqlDataHelper.getClass().getSimpleName(),
                                        sqlDataHelper.getDbName(),
                                        StringUtils.padLeft(insertCount, 19, ' '),
                                        StringUtils.padLeft(diff / 100f, 19, ' '),
                                        StringUtils.padLeft(insertCount / (diff / 100f) * 1000, 19, ' '),
                                        StringUtils.padLeft(executeCount, 19, ' '),
                                        StringUtils.padLeft(executeDiffTime / 100f, 19, ' '),
                                        StringUtils.padLeft(executeCount / (executeDiffTime / 100f) * 1000, 19, ' ')
                                )
                );
            }
        }

        protected int executeUpdate(String sql, List<Object[]> paramList) {
            int insertCount = 0;
            try (Connection connection = sqlDataHelper.connection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    for (Object[] objects : paramList) {
                        for (int i = 0; i < objects.length; i++) {
                            Object object = objects[i];
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
                for (Object[] objects : paramList) {
                    try (Connection connection = sqlDataHelper.connection();
                         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        for (int i = 0; i < objects.length; i++) {
                            Object object = objects[i];
                            preparedStatement.setObject(i + 1, object);
                        }
                        preparedStatement.executeUpdate();
                        insertCount++;
                    } catch (Exception e1) {
                        File file = new File("target/db_error/" + sqlDataHelper.getDbName() + "/" + this.threadId + "_" + System.nanoTime() + ".txt");
                        String formatted = "db batch error\nsql: %s\ndata: %s\n\nerror: \n%s".formatted(sql, FastJsonUtil.toJson(objects), e1.toString());
                        FileWriteUtil.writeString(file, formatted, true);
                        log.error("{}", formatted, e1);
                    }
                }
            }
            return insertCount;
        }
    }
}
