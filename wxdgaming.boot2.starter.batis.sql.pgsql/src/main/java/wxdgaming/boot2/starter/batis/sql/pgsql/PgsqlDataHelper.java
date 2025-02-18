package wxdgaming.boot2.starter.batis.sql.pgsql;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlConfig;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据集
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 12:36
 **/
@Slf4j
@Getter
@Setter
public class PgsqlDataHelper extends SqlDataHelper<PgSqlDDLBuilder> {

    public PgsqlDataHelper(SqlConfig sqlConfig) {
        super(sqlConfig, new PgSqlDDLBuilder());
    }

    @Override public void initBatch() {
        this.sqlDataBatch = new PgsqlDataBatch(this);
    }

    /** 查询当前数据库所有的表 key: 表名字, value: 表备注 */
    @Override public Map<String, String> findTableMap() {
        Map<String, String> dbTableMap = new LinkedHashMap<>();
        String sql = """
                SELECT c.relname AS table_name, obj_description(c.oid, 'pg_class') AS table_comment
                FROM pg_class c
                JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE c.relkind = 'r' AND n.nspname = 'public'
                """;
        final List<JSONObject> jsonObjects = this.query(sql);
        for (JSONObject jsonObject : jsonObjects) {
            final String table_name = jsonObject.getString("table_name");
            final String TABLE_COMMENT = jsonObject.getString("table_comment");
            dbTableMap.put(table_name, TABLE_COMMENT);
        }
        return dbTableMap;
    }

    /** 查询所有表的结构，key: 表名字, value: { key: 字段名字, value: 字段结构 } */
    @Override public Map<String, LinkedHashMap<String, JSONObject>> findTableStructMap() {
        String pgsql = """
                SELECT
                c.relname as table_name,
                a.attname AS column_name,
                a.attnum as ordinal_position,
                t.typname AS column_type,
                a.attlen AS length,
                a.atttypmod AS lengthvar,
                a.attnotnull AS notnull,
                b.description AS column_comment
                FROM pg_class c, pg_attribute a
                LEFT JOIN pg_description b ON a.attrelid = b.objoid AND a.attnum = b.objsubid, pg_type t
                WHERE a.attnum > 0
                AND c.relnamespace = 'public'::regnamespace
                AND a.attrelid = c.oid
                AND a.atttypid = t.oid
                ORDER BY c.relname,a.attnum
                """;
        LinkedHashMap<String, LinkedHashMap<String, JSONObject>> dbTableStructMap = new LinkedHashMap<>();
        final List<JSONObject> jsonObjects = this.query(pgsql);
        for (JSONObject jsonObject : jsonObjects) {
            final String table_name = jsonObject.getString("table_name");
            final String column_name = jsonObject.getString("column_name");
            final String column_type = jsonObject.getString("column_type");
            final int lengthvar = jsonObject.getIntValue("lengthvar");

            // Adjust lengthvar for VARCHAR type
            if ("varchar".equalsIgnoreCase(column_type)) {
                int len = lengthvar - 4;
                jsonObject.put("lengthvar", len);
                jsonObject.put("column_type", "varchar(%s)".formatted(len));
            }

            dbTableStructMap.computeIfAbsent(table_name, l -> new LinkedHashMap<>())
                    .put(column_name, jsonObject);
        }
        return dbTableStructMap;
    }

    @Override public void checkTable(Map<String, LinkedHashMap<String, JSONObject>> databseTableMap, TableMapping tableMapping, String tableName, String tableComment) {
        super.checkTable(databseTableMap, tableMapping, tableName, tableComment);

        /*TODO 处理索引*/
        LinkedHashMap<String, TableMapping.FieldMapping> columnMap = tableMapping.getColumns();
        for (TableMapping.FieldMapping fieldMapping : columnMap.values()) {
            if (fieldMapping.isIndex()) {
                String keyName = tableName + "_" + fieldMapping.getColumnName();
                /*pgsql 默认全小写*/
                keyName = keyName.toLowerCase();
                String checkIndexSql = "SELECT 1 as exists FROM pg_indexes WHERE tablename = '%s' AND indexname = '%s';".formatted(tableName, keyName);
                Integer scalar = executeScalar(checkIndexSql, Integer.class);
                if (scalar == null || scalar != 1) {
                    String alterColumn = ddlBuilder.buildAlterColumnIndex(tableName, fieldMapping);
                    executeUpdate(alterColumn);
                    log.warn("pgsql 数据库 {}，新增索引：{}", getSqlConfig().dbName(), keyName);
                }
            }
        }

        tableMapping.getColumns().values().forEach(fieldMapping -> {
            updateColumnComment(tableName, fieldMapping);
        });
    }

    @Override protected void createTable(TableMapping tableMapping, String tableName, String comment) {
        StringBuilder stringBuilder = ddlBuilder.buildTableSqlString(tableMapping, tableName);
        String string = stringBuilder.toString();
        string = ddlBuilder.buildSql$$(string);
        this.executeUpdate(string);
        this.executeUpdate("COMMENT ON TABLE \"%s\" IS '%s';".formatted(tableName, comment));
        log.warn("创建表：{}", tableName);
    }

    /** 添加分区 */
    public void addPartition(String tableName, String from, String to) {
        addPartition(findTableMap(), tableName, from, to);
    }

    /** 添加分区 */
    public void addPartition(Map<String, String> dbTableMap, String tableName, String from, String to) {
        String partition_table_name = tableName + "_" + from;
        if (dbTableMap.containsKey(partition_table_name))
            return;
        String string = "CREATE TABLE \"%s\" PARTITION OF \"%s\" FOR VALUES FROM (%s) TO (%s);"
                .formatted(partition_table_name, tableName, from, to);
        executeUpdate(string);
        dbTableMap.put(partition_table_name, partition_table_name);
        log.info("数据库 {} 表 {} 创建分区 {}", sqlConfig.dbName(), tableName, partition_table_name);
    }

    @Override protected void addColumn(String tableName, TableMapping.FieldMapping fieldMapping) {
        String sql = "ALTER TABLE \"%s\" ADD COLUMN \"%s\" %s;"
                .formatted(
                        tableName,
                        fieldMapping.getColumnName(),
                        ddlBuilder.buildColumnDefinition(fieldMapping)
                );
        executeUpdate(sql);
        updateColumnComment(tableName, fieldMapping);
    }

    @Override protected void updateColumn(String tableName, JSONObject dbColumnMapping, TableMapping.FieldMapping fieldMapping) {
        String columnDefinition = ddlBuilder.buildColumnDefinition(fieldMapping);
        String[] split = columnDefinition.split(" ");
        String columnType = split[0].toLowerCase();
        if (columnType.equalsIgnoreCase(dbColumnMapping.getString("column_type"))) {
            return;
        }
        String sql = "ALTER TABLE \"%s\" ALTER COLUMN \"%s\" TYPE %s;"
                .formatted(
                        tableName,
                        fieldMapping.getColumnName(),
                        columnDefinition
                );
        executeUpdate(sql);
        updateColumnComment(tableName, fieldMapping);
    }

    protected void updateColumnComment(String tableName, TableMapping.FieldMapping fieldMapping) {
        String sql = "COMMENT ON COLUMN \"%s\".\"%s\" IS '%s';"
                .formatted(
                        tableName,
                        fieldMapping.getColumnName(),
                        fieldMapping.getComment()
                );
        executeUpdate(sql);
    }

}
