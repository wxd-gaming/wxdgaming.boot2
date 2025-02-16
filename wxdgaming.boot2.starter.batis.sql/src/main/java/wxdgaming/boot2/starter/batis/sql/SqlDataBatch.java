package wxdgaming.boot2.starter.batis.sql;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.collection.SplitCollection;
import wxdgaming.boot2.core.collection.Table;
import wxdgaming.boot2.starter.batis.DataBatch;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
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
        for (int i = 0; i < 1; i++) {
            batchThreads.add(new BatchThread());
        }
    }

    public <SDH extends SqlDataHelper<?>> SDH dataHelper() {
        return (SDH) sqlDataHelper;
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
        protected Table<String, String, SplitCollection<Object[]>> batchInsertMap = new Table<>();
        protected Table<String, String, SplitCollection<Object[]>> batchUpdateMap = new Table<>();

        public BatchThread() {
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
            Collection<HashMap<String, SplitCollection<Object[]>>> values = tmp.values();
            for (HashMap<String, SplitCollection<Object[]>> map : values) {
                for (Map.Entry<String, SplitCollection<Object[]>> entry : map.entrySet()) {
                    LinkedList<List<Object[]>> es = entry.getValue().getEs();
                    for (List<Object[]> list : es) {
                        try (Connection connection = sqlDataHelper.connection()) {
                            connection.setAutoCommit(false);
                            try (PreparedStatement preparedStatement = connection.prepareStatement(entry.getKey())) {
                                for (Object[] objects : list) {
                                    for (int i = 0; i < objects.length; i++) {
                                        Object object = objects[i];
                                        preparedStatement.setObject(i + 1, object);
                                    }
                                    preparedStatement.addBatch();
                                    preparedStatement.clearParameters();
                                }
                                preparedStatement.executeBatch();
                            }
                            connection.commit();
                        } catch (Exception e) {
                            log.error("sqlDataBatch error", e);
                        }
                    }
                }
            }
        }

    }


}
