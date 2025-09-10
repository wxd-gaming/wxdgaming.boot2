package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Stop;
import wxdgaming.boot2.core.ann.StopBefore;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.DataHelper;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * 数据集
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:36
 **/
@Slf4j
@Getter
@Setter
public abstract class SqlDataHelper extends DataHelper {

    protected final SqlConfig sqlConfig;
    protected final HikariDataSource hikariDataSource;
    protected SqlDataBatch dataBatch;
    protected final SqlDataCacheService cacheService;

    public SqlDataHelper(SqlConfig sqlConfig, SqlDDLBuilder ddl) {
        super(ddl);
        this.sqlConfig = sqlConfig;
        this.sqlConfig.createDatabase();
        this.hikariDataSource = sqlConfig.hikariDataSource();
        if (sqlConfig.getBatchThreadSize() > 0) {
            initDataBatch();
        }
        cacheService = new SqlDataCacheService(this);
    }

    @SuppressWarnings("unchecked")
    @Override public SqlDDLBuilder ddlBuilder() {
        return super.ddlBuilder();
    }

    @Start()
    @Order(-1)
    public void start() {
        if (sqlConfig.getScanPackage() != null && sqlConfig.getScanPackage().length > 0) {
            Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = findTableStructMap();
            ReflectProvider.Builder.of(sqlConfig.getScanPackage()).build()
                    .classWithAnnotated(DbTable.class)
                    .forEach(cls -> {
                        if (!Entity.class.isAssignableFrom(cls)) {
                            throw new RuntimeException(cls + " not super " + Entity.class);
                        }

                        checkTable(tableStructMap, (Class<? extends Entity>) cls, true);
                    });
        }
    }

    @Order(100/*优先清空缓存*/)
    @StopBefore
    public void stopCache() {
        log.info("关闭数据库缓存：{}", this.getSqlConfig().getUrl());
        if (this.cacheService != null)
            this.cacheService.stop();
    }

    @Stop
    @Order(Integer.MAX_VALUE/*最后关闭*/)
    @Override public void stop() {
        log.info("准备关闭数据库服务：{}", this.getSqlConfig().getUrl());
        if (this.dataBatch != null)
            this.dataBatch.stop();
        this.hikariDataSource.close();
    }

    public abstract void initDataBatch();

    public abstract SqlQueryBuilder queryBuilder();

    @SuppressWarnings("unchecked")
    public <SDB extends SqlDataBatch> SDB dataBatch() {
        return (SDB) dataBatch;
    }

    public String getDbName() {
        return sqlConfig.dbName();
    }

    public void checkTable(Class<? extends Entity> cls) {
        TableMapping tableMapping = tableMapping(cls);
        if (tableMapping == null) {
            throw new RuntimeException("表映射关系不存在");
        }
        String tableName = tableMapping.getTableName();
        checkTable(tableMapping, tableName, tableMapping.getTableComment(), true);
    }

    public void checkTable(Class<? extends Entity> cls, String tableName, String tableComment, boolean actionPartition) {
        TableMapping tableMapping = tableMapping(cls);
        if (tableMapping == null) {
            throw new RuntimeException("表映射关系不存在");
        }
        checkTable(tableMapping, tableName, tableComment, actionPartition);
    }

    public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> tableStructMap, Class<? extends Entity> cls, boolean actionPartition) {
        TableMapping tableMapping = tableMapping(cls);
        if (tableMapping == null) {
            throw new RuntimeException("表映射关系不存在");
        }
        String tableName = tableMapping.getTableName();
        checkTable(tableStructMap, tableMapping, tableName, tableMapping.getTableComment(), actionPartition);
    }

    public void checkTable(TableMapping tableMapping, String tableName, String tableComment, boolean actionPartition) {
        Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = findTableStructMap();
        checkTable(tableStructMap, tableMapping, tableName, tableComment, actionPartition);
    }

    public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> databseTableMap, TableMapping tableMapping, String tableName, String tableComment, boolean actionPartition) {
        final LinkedHashMap<String, JSONObject> tableColumns = databseTableMap.get(tableName);
        if (tableColumns == null) {
            createTable(tableMapping, tableName, tableComment, actionPartition);
        } else {
            tableMapping.getColumns().values().forEach(fieldMapping -> {
                JSONObject dbColumnMapping = tableColumns.get(fieldMapping.getColumnName());
                if (dbColumnMapping == null) {
                    addColumn(tableName, fieldMapping);
                } else {
                    updateColumn(tableName, dbColumnMapping, fieldMapping);
                }
            });
        }
    }

    protected void createTable(TableMapping tableMapping, String tableName, String comment, boolean actionPartition) {
        StringBuilder stringBuilder = ddlBuilder().buildTableSqlString(tableMapping, tableName, actionPartition);
        this.executeUpdate(stringBuilder.toString());
        log.warn("创建表：{}", tableName);
    }

    protected void addColumn(String tableName, TableMapping.FieldMapping fieldMapping) {
        String sql = "ALTER TABLE %s ADD COLUMN %s %s COMMENT '%s'".formatted(
                tableName,
                fieldMapping.getColumnName(),
                ddlBuilder().buildColumnDefinition(fieldMapping),
                fieldMapping.getComment()
        );
        executeUpdate(sql);
    }

    protected void updateColumn(String tableName, JSONObject dbColumnMapping, TableMapping.FieldMapping fieldMapping) {
        String columnDefinition = ddlBuilder().buildColumnDefinition(fieldMapping);
        String[] split = columnDefinition.split(" ");
        String columnType = split[0].toLowerCase();
        if (dbColumnMapping.getString("COLUMN_TYPE").equalsIgnoreCase(columnType)) {
            return;
        }
        String sql = "ALTER TABLE %s MODIFY COLUMN %s %s COMMENT '%s';".formatted(
                tableName,
                fieldMapping.getColumnName(),
                columnDefinition,
                fieldMapping.getComment()
        );
        executeUpdate(sql);
    }


    /** 查询当前数据库所有的表 key: 表名字, value: 表备注 */
    public Map<String, String> findTableMap() {
        Map<String, String> dbTableMap = new LinkedHashMap<>();
        String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.`TABLES` WHERE table_schema= ? ORDER BY TABLE_NAME";
        this.query(sql, new Object[]{this.getDbName()}, row -> {
            final String table_name = row.getString("TABLE_NAME");
            final String TABLE_COMMENT = row.getString("TABLE_COMMENT");
            dbTableMap.put(table_name, TABLE_COMMENT);
            return false;
        });
        return dbTableMap;
    }

    /** 查询所有表的结构，key: 表名字, value: { key: 字段名字, value: 字段结构 } */
    public Map<String, LinkedHashMap<String, JSONObject>> findTableStructMap() {
        LinkedHashMap<String, LinkedHashMap<String, JSONObject>> dbTableStructMap = new LinkedHashMap<>();
        String sql =
                "SELECT" +
                "    TABLE_NAME," +
                "    COLUMN_NAME," +
                "    ORDINAL_POSITION," +
                "    COLUMN_DEFAULT," +
                "    IS_NULLABLE," +
                "    DATA_TYPE," +
                "    CHARACTER_MAXIMUM_LENGTH," +
                "    NUMERIC_PRECISION," +
                "    NUMERIC_SCALE," +
                "    COLUMN_TYPE," +
                "    COLUMN_KEY," +
                "    EXTRA," +
                "    COLUMN_COMMENT \n" +
                "FROM information_schema.`COLUMNS`\n" +
                "WHERE table_schema= ? \n" +
                "ORDER BY TABLE_NAME, ORDINAL_POSITION;";

        this.query(sql, new Object[]{this.getDbName()}, row -> {
            final String table_name = row.getString("TABLE_NAME");
            final String column_name = row.getString("COLUMN_NAME");
            dbTableStructMap
                    .computeIfAbsent(table_name, l -> new LinkedHashMap<>())
                    .put(column_name, row);
            return true;
        });
        return dbTableStructMap;
    }

    @Override public Connection connection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public <R extends Entity> long tableCount(Class<R> cls) {
        String tableName = TableMapping.tableName(cls);
        return tableCount(tableName);
    }

    public <R extends Entity> long tableCount(Class<R> cls, String where, Object... args) {
        String tableName = TableMapping.tableName(cls);
        return tableCount(tableName, where, args);
    }

    @Override public long tableCount(String tableName) {
        return tableCount(tableName, "");
    }

    public long tableCount(String tableName, String where, Object... args) {
        String sql = "SELECT COUNT(*) FROM %s".formatted(tableName);
        if (StringUtils.isNotBlank(where)) {
            sql += " WHERE " + where;
        }
        return tableCountBySql(sql, args);
    }

    public long tableCountBySql(String sql, Object... args) {
        Long scalar = executeScalar(sql, Long.class, args);
        if (scalar == null)
            return 0;
        return scalar;
    }

    /**
     * 执行sql语句
     *
     * @param sql    需要执行的 sql 语句
     * @param params 参数列表
     * @return 执行影响的行数
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-21 13:35
     */
    public int executeUpdate(String sql, Object... params) {
        AssertUtil.assertTrue(StringUtils.isNotBlank(sql), "sql 语句不能为空");
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    statement.setObject(i + 1, param);
                }
            }
            if (sqlConfig.isDebug()) {
                log.info(
                        "\n{} executeUpdate sql: \n{}",
                        getDbName(), statement.toString()
                );
            }
            int i = statement.executeUpdate();
            if (!connection.getAutoCommit())
                connection.commit();
            return i;
        } catch (Exception e) {
            throw Throw.of(getDbName() + " " + sql + " " + Objects.toString(params), e);
        }
    }

    /** 返回第一行，第一列 */
    public <R> R executeScalar(String sql, Class<R> cls, Object... params) {
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, params)) {
            return sqlQueryResult.scalar(cls);
        }
    }

    /** 返回第一列 */
    public <R> List<R> executeScalarList(String sql, Class<R> cls, Object... params) {
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, params)) {
            return sqlQueryResult.scalarList(cls);
        }
    }

    @Override public List<JSONObject> queryListByEntity(Class<? extends Entity> cls) {
        TableMapping tableMapping = tableMapping(cls);
        return queryListByTableName(tableMapping.getTableName());
    }

    @Override public List<JSONObject> queryListByTableName(String tableName) {
        return queryList("select * from " + tableName);
    }

    /**
     * 根据实体查询列表
     *
     * @param cls      实体类
     * @param sqlWhere sql 语句的 where 条件
     * @param args
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-21 09:18
     */
    public List<JSONObject> queryListByEntityWhere(Class<? extends Entity> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return queryList(sql, args);
    }

    public List<JSONObject> queryList(String sql, Object... params) {
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, params)) {
            return sqlQueryResult.rowList();
        }
    }

    /** 返回第一条 */
    public JSONObject queryTop(String sql, Object... params) {
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, params)) {
            if (sqlQueryResult.hasNext()) {
                return sqlQueryResult.row();
            }
        }
        return null;
    }

    public void query(String sql, Object[] params, Predicate<JSONObject> consumer) {
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, params)) {
            while (sqlQueryResult.hasNext()) {
                if (!consumer.test(sqlQueryResult.row()))
                    return;
            }
        }
    }

    public SqlQueryResult queryResultSet(String sql, Object... params) {
        return new SqlQueryResult(this, sql, params);
    }

    @Override public <R extends Entity> List<R> findList(Class<R> cls) {
        TableMapping tableMapping = tableMapping(cls);
        return findList(tableMapping.getTableName(), cls);
    }

    @Override public <R extends Entity> List<R> findList(String tableName, Class<R> cls) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableMapping.getTableName());
        return findListBySql(cls, sql);
    }

    /**
     * 查询表数据
     *
     * @param cls      返回的数据实体类
     * @param sqlWhere where 条件
     * @param args     参数
     * @param <R>      实体模型
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-16 01:14
     */
    public <R extends Entity> List<R> findListByWhere(Class<R> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return findListBySql(cls, sql, args);
    }

    /**
     * 查询表数据
     *
     * @param cls  返回的数据实体类
     * @param sql  查询的sql
     * @param args 参数
     * @param <R>  实体模型
     * @return 查询结果
     */
    public <R extends Entity> List<R> findListBySql(Class<R> cls, String sql, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        List<R> ret = new ArrayList<>();
        try (SqlQueryResult sqlQueryResult = this.queryResultSet(sql, args)) {
            while (sqlQueryResult.hasNext()) {
                R entity = ddlBuilder().data2Object(tableMapping, sqlQueryResult.row());
                ret.add(entity);
            }
        }
        return ret;
    }

    @Override public <R extends Entity> R findByKey(Class<R> cls, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        return findByKey(tableMapping.getTableName(), cls, args);
    }

    @Override public <R extends Entity> R findByKey(String tableName, Class<R> cls, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableMapping.getTableName());
        String where = ddlBuilder().buildKeyWhere(tableMapping);
        sql += " where " + where;
        return findBySql(cls, sql, args);
    }

    /**
     * 根据主键值查询
     *
     * @param cls  返回的数据实体类
     * @param args 参数
     * @param <R>  实体模型
     * @return
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2025-02-16 01:15
     */
    public <R extends Entity> R findByWhere(Class<R> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return findBySql(cls, sql, args);
    }

    public <R extends Entity> R findByWhere(String tableName, Class<R> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder().buildSelectSql(tableMapping, tableName);
        sql += " where " + sqlWhere;
        return findBySql(cls, sql, args);
    }

    public <R extends Entity> R findBySql(Class<R> cls, String sql, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        AtomicReference<R> ret = new AtomicReference<>();
        this.query(sql, args, row -> {
            R entity = ddlBuilder().data2Object(tableMapping, row);
            ret.set(entity);
            return false;
        });
        return ret.get();
    }

    @Override public boolean existBean(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String exitSql = ddlBuilder().buildExitSql(entity);
        Integer scalar = executeScalar(exitSql, Integer.class, ddlBuilder().buildKeyParams(tableMapping, entity));
        if (scalar != null && scalar == 1) {
            entity.setNewEntity(false);
            return true;
        }
        return false;
    }

    @Override public void insert(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String tableName = TableMapping.beanTableName(entity);
        String insert = ddlBuilder().buildInsertSql(tableMapping, tableName);
        Object[] insertParams = ddlBuilder().buildInsertParams(tableMapping, entity);
        this.executeUpdate(insert, insertParams);
    }

    @Override public void update(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String tableName = TableMapping.beanTableName(entity);
        String sql = ddlBuilder().buildUpdateSql(tableMapping, tableName);
        Object[] objects = ddlBuilder().builderUpdateParams(tableMapping, entity);
        this.executeUpdate(sql, objects);
    }

    @Override public void delete(Entity entity) {
        TableMapping tableMapping = ddlBuilder().tableMapping(entity.getClass());
        executeUpdate(ddlBuilder().buildDeleteSql(tableMapping, tableMapping.getTableName()), ddlBuilder().buildKeyParams(tableMapping, entity));
    }

    @Override public <R extends Entity> void deleteByKey(Class<R> cls, Object... args) {
        TableMapping tableMapping = ddlBuilder().tableMapping(cls);
        executeUpdate(ddlBuilder().buildDeleteSql(tableMapping, tableMapping.getTableName()), args);
    }

    @Override public <R extends Entity> void deleteByKey(String tableName, Class<R> cls, Object... args) {
        TableMapping tableMapping = ddlBuilder().tableMapping(cls);
        executeUpdate(ddlBuilder().buildDeleteSql(tableMapping, tableName), args);
    }

    @Override public <R extends Entity> void deleteByWhere(Class<R> cls, String where, Object... args) {
        TableMapping tableMapping = ddlBuilder().tableMapping(cls);
        deleteByWhere(tableMapping.getTableName(), where, args);
    }

    @Override public void deleteByWhere(String tableName, String where, Object... args) {
        String sql = "delete from `" + tableName + "` where " + where;
        String string = ddlBuilder().buildSql$$(sql);
        executeUpdate(string, args);
    }

    @Override public void truncates() {
        findTableMap().forEach((tableName, tableComment) -> {
            truncate(tableName);
        });
    }

    @Override public <R extends Entity> void truncate(Class<R> cls) {
        super.truncate(cls);
    }

    @Override public void truncate(String tableName) {
        String sqlStr = "TRUNCATE TABLE `" + tableName + "`";
        String string = ddlBuilder().buildSql$$(sqlStr);
        executeUpdate(string);
    }

    @Override public String toString() {
        return "%s{url=%s}".formatted(this.getClass().getSimpleName(), this.getSqlConfig().getUrl());
    }

}
