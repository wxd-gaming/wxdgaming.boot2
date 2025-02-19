package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.starter.batis.DataHelper;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
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
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Slf4j
@Getter
@Setter
public abstract class SqlDataHelper<DDL extends SqlDDLBuilder> extends DataHelper<DDL> {

    protected final SqlConfig sqlConfig;
    protected final HikariDataSource hikariDataSource;
    protected SqlDataBatch sqlDataBatch;

    public SqlDataHelper(SqlConfig sqlConfig, DDL ddl) {
        super(ddl);
        this.sqlConfig = sqlConfig;
        this.sqlConfig.createDatabase();
        this.hikariDataSource = sqlConfig.hikariDataSource();
        initBatch();
    }

    @Start()
    @Sort(100)
    public void start() {
        if (StringUtils.isNotBlank(sqlConfig.getScanPackage())) {
            Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = findTableStructMap();
            ReflectContext.Builder.of(sqlConfig.getScanPackage()).build()
                    .classWithAnnotated(DbTable.class)
                    .forEach(cls -> {
                        if (!Entity.class.isAssignableFrom(cls)) {
                            throw new RuntimeException(cls + " not super " + Entity.class);
                        }

                        checkTable(tableStructMap, (Class<? extends Entity>) cls);
                    });
        }
    }

    public abstract void initBatch();

    public <SDB extends SqlDataBatch> SDB dataBatch() {
        return (SDB) sqlDataBatch;
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
        checkTable(tableMapping, tableName, tableMapping.getTableComment());
    }

    public void checkTable(Class<? extends Entity> cls, String tableName, String tableComment) {
        TableMapping tableMapping = tableMapping(cls);
        if (tableMapping == null) {
            throw new RuntimeException("表映射关系不存在");
        }
        checkTable(tableMapping, tableName, tableComment);
    }

    public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> tableStructMap, Class<? extends Entity> cls) {
        TableMapping tableMapping = tableMapping(cls);
        if (tableMapping == null) {
            throw new RuntimeException("表映射关系不存在");
        }
        String tableName = tableMapping.getTableName();
        checkTable(tableStructMap, tableMapping, tableName, tableMapping.getTableComment());
    }

    public void checkTable(TableMapping tableMapping, String tableName, String tableComment) {
        Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = findTableStructMap();
        checkTable(tableStructMap, tableMapping, tableName, tableComment);
    }

    public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> databseTableMap, TableMapping tableMapping, String tableName, String tableComment) {
        final LinkedHashMap<String, JSONObject> tableColumns = databseTableMap.get(tableName);
        if (tableColumns == null) {
            createTable(tableMapping, tableName, tableComment);
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

    protected void createTable(TableMapping tableMapping, String tableName, String comment) {
        StringBuilder stringBuilder = ddlBuilder.buildTableSqlString(tableMapping, tableName);
        this.executeUpdate(stringBuilder.toString());
        log.warn("创建表：{}", tableName);
    }

    protected void addColumn(String tableName, TableMapping.FieldMapping fieldMapping) {
        String sql = "ALTER TABLE %s ADD COLUMN %s %s COMMENT '%s'".formatted(
                tableName,
                fieldMapping.getColumnName(),
                ddlBuilder.buildColumnDefinition(fieldMapping),
                fieldMapping.getComment()
        );
        executeUpdate(sql);
    }

    protected void updateColumn(String tableName, JSONObject dbColumnMapping, TableMapping.FieldMapping fieldMapping) {
        String columnDefinition = ddlBuilder.buildColumnDefinition(fieldMapping);
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

    @Override public <R extends Entity> int tableCount(Class<R> cls) {
        String tableName = TableMapping.tableName(cls);
        return tableCount(tableName);
    }

    @Override public <R extends Entity> int tableCount(Class<R> cls, String where, Object... args) {
        String tableName = TableMapping.tableName(cls);
        return tableCount(tableName, where, args);
    }

    @Override public int tableCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM %s".formatted(tableName);
        Integer scalar = executeScalar(sql, Integer.class);
        if (scalar == null)
            return 0;
        return scalar;
    }

    @Override public int tableCount(String tableName, String where, Object... args) {
        String sql = "SELECT COUNT(*) FROM %s".formatted(tableName);
        if (StringUtils.isNotBlank(where)) {
            sql += " WHERE " + where;
        }
        Integer scalar = executeScalar(sql, Integer.class, args);
        if (scalar == null)
            return 0;
        return scalar;
    }

    public int executeUpdate(String sql, Object... params) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    statement.setObject(i + 1, param);
                }
            }
            if (sqlConfig.isDebug()) {
                log.info(
                        "\nexecuteUpdate sql: \n{}",
                        statement.toString()
                );
            }
            int i = statement.executeUpdate();
            if (!connection.getAutoCommit())
                connection.commit();
            return i;
        } catch (Exception e) {
            throw Throw.of(getDbName() + " " + sql, e);
        }
    }

    /** 返回第一行，第一列 */
    public <R> R executeScalar(String sql, Class<R> cls, Object... params) {
        AtomicReference<R> ret = new AtomicReference<>();
        this.queryResultSet(sql, params, resultSet -> {
            try {
                Object object = resultSet.getObject(1);
                if (cls.isAssignableFrom(object.getClass())) {
                    ret.set(cls.cast(object));
                } else {
                    ret.set(FastJsonUtil.parse(String.valueOf(object), cls));
                }
                return false;
            } catch (SQLException e) {
                throw Throw.of(getDbName() + " " + sql, e);
            }
        });
        return ret.get();
    }

    public List<JSONObject> queryList(String sql, Object... params) {
        List<JSONObject> rows = new ArrayList<>();
        this.query(sql, params, rows::add);
        return rows;
    }

    public void query(String sql, Object[] params, Predicate<JSONObject> consumer) {
        this.queryResultSet(sql, params, resultSet -> {
            try {
                JSONObject jsonObject = new JSONObject();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    jsonObject.put(columnName, columnValue);
                }
                if (!consumer.test(jsonObject))
                    return false;
                return true;
            } catch (Exception e) {
                throw Throw.of(getDbName() + " " + sql, e);
            }
        });

    }

    public void queryResultSet(String sql, Object[] params, Predicate<java.sql.ResultSet> consumer) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    statement.setObject(i + 1, param);
                }
            }

            if (sqlConfig.isDebug()) {
                log.info(
                        "\nquery sql: \n{}",
                        statement.toString()
                );
            }

            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (!consumer.test(resultSet))
                        break;
                }
            }
        } catch (Exception e) {
            throw Throw.of(getDbName() + " " + sql, e);
        }
    }

    @Override public List<JSONObject> queryListByEntity(Class<? extends Entity> cls) {
        TableMapping tableMapping = tableMapping(cls);
        return queryListByTableName(tableMapping.getTableName());
    }

    @Override public List<JSONObject> queryListByEntityWhere(Class<? extends Entity> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder.buildSelect(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return queryList(sql, args);
    }

    @Override public List<JSONObject> queryListByTableName(String tableName) {
        return queryList("select * from " + tableName);
    }

    @Override public <R extends Entity> List<R> findList(Class<R> cls) {
        TableMapping tableMapping = tableMapping(cls);
        return findList(tableMapping.getTableName(), cls);
    }

    @Override public <R extends Entity> List<R> findList(String tableName, Class<R> cls) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder.buildSelect(tableMapping, tableMapping.getTableName());
        return findListBySql(cls, sql);
    }

    @Override public <R extends Entity> List<R> findListByWhere(Class<R> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder.buildSelect(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return findListBySql(cls, sql, args);
    }

    @Override public <R extends Entity> List<R> findListBySql(Class<R> cls, String sql, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        List<R> ret = new ArrayList<>();
        query(sql, args, row -> {
            R entity = ddlBuilder.data2Object(tableMapping, row);
            ret.add(entity);
            return true;
        });
        return ret;
    }

    @Override public <R extends Entity> R findById(Class<R> cls, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        return findById(tableMapping.getTableName(), cls, args);
    }

    @Override public <R extends Entity> R findById(String tableName, Class<R> cls, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder.buildSelect(tableMapping, tableMapping.getTableName());
        String where = ddlBuilder.buildKeyWhere(tableMapping);
        sql += " where " + where;
        return findBySql(cls, sql, args);
    }

    @Override public <R extends Entity> R findByWhere(Class<R> cls, String sqlWhere, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        String sql = ddlBuilder.buildSelect(tableMapping, tableMapping.getTableName());
        sql += " where " + sqlWhere;
        return findBySql(cls, sql, args);
    }

    @Override public <R extends Entity> R findBySql(Class<R> cls, String sql, Object... args) {
        TableMapping tableMapping = tableMapping(cls);
        AtomicReference<R> ret = new AtomicReference<>();
        this.query(sql, args, row -> {
            R entity = ddlBuilder.data2Object(tableMapping, row);
            ret.set(entity);
            return false;
        });
        return ret.get();
    }

    @Override public boolean existBean(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String exitSql = ddlBuilder.buildExitSql(entity);
        Integer scalar = executeScalar(exitSql, Integer.class, ddlBuilder.buildKeyParams(tableMapping, entity));
        if (scalar != null && scalar == 1) {
            entity.setNewEntity(false);
            return true;
        }
        return false;
    }

    @Override public void insert(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String tableName = TableMapping.beanTableName(entity);
        String insert = ddlBuilder.buildInsert(tableMapping, tableName);
        Object[] insertParams = ddlBuilder.buildInsertParams(tableMapping, entity);
        this.executeUpdate(insert, insertParams);
    }

    @Override public void update(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String tableName = TableMapping.beanTableName(entity);
        String sql = ddlBuilder.buildUpdate(tableMapping, tableName);
        Object[] objects = ddlBuilder.builderUpdateParams(tableMapping, entity);
        this.executeUpdate(sql, objects);
    }

}
