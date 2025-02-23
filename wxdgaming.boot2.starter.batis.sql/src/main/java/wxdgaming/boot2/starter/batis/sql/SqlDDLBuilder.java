package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.DDLBuilder;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * sql ddl
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 17:38
 **/
public abstract class SqlDDLBuilder extends DDLBuilder {

    @Override public TableMapping tableMapping(Class<? extends Entity> cls) {
        TableMapping tableMapping = super.tableMapping(cls);
        long count = tableMapping.getColumns().values().stream().filter(v -> AnnUtil.ann(v.getField(), Partition.class) != null).count();
        if (count > 1) {
            throw new RuntimeException("一个实体类只能有一个分区字段");
        }
        return tableMapping;
    }

    public StringBuilder buildTableSqlString(TableMapping tableMapping, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE `").append(tableName).append("` (").append("\n");
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
            sb.append("`").append(fieldMapping.getColumnName()).append("`").append(" ").append(buildColumnDefinition(fieldMapping)).append(",").append("\n");
        }
        List<TableMapping.FieldMapping> keyFields = tableMapping.getKeyFields();
        sb.append("PRIMARY KEY (");
        for (int i = 0; i < keyFields.size(); i++) {
            TableMapping.FieldMapping keyField = keyFields.get(i);
            sb.append("`").append(keyField.getColumnName()).append("`");
            if (i < keyFields.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")").append("\n");
        sb.append(")");
        return sb;
    }

    /** 构建字段类型 */
    public String buildColumnDefinition(TableMapping.FieldMapping fieldMapping) {
        ColumnType columnType = fieldMapping.getColumnType();
        String columnDefinition = null;
        switch (columnType) {
            case Bool, Byte, Short -> {
                columnDefinition = "TINYINT";
            }
            case Int -> {
                columnDefinition = "INT";
            }
            case Long -> {
                columnDefinition = "bigint";
            }
            case Float -> {
                columnDefinition = "FLOAT";
            }
            case Double -> {
                columnDefinition = "DOUBLE";
            }
            case String -> {
                if (fieldMapping.getLength() < 5000)
                    columnDefinition = "VARCHAR(%s)".formatted(fieldMapping.getLength());
                else if (fieldMapping.getLength() < 10240)
                    columnDefinition = "text";
                else
                    columnDefinition = "longtext";
            }
            case Blob -> {
                int length = fieldMapping.getLength();
                if (length <= 255) {
                    columnDefinition = "TINYBLOB";
                } else if (length <= 65535) {
                    columnDefinition = "BLOB";
                } else if (length <= 16777215) {
                    columnDefinition = "MEDIUMBLOB";
                } else {
                    columnDefinition = "LONGBLOB";
                }
            }
            case Json -> {
                columnDefinition = "JSON";
            }
            case null, default -> {
                throw new RuntimeException("无法处理的数据库类型 " + columnType);
            }
        }
        if (!fieldMapping.isNullable()) {
            columnDefinition += " NOT NULL";
        }
        return columnDefinition;
    }

    /**
     * 构建列的索引信息 alter table add
     *
     * @param tableName    表名
     * @param fieldMapping 映射
     * @return
     */
    public String buildAlterColumnIndex(String tableName, TableMapping.FieldMapping fieldMapping) {
        String columnName = fieldMapping.getColumnName();
        return "ALTER TABLE `%s` ADD INDEX %s_%s (`%s`);".formatted(tableName, tableName, columnName, columnName);
    }

    /** ? 占位符 */
    public String build$$(TableMapping.FieldMapping fieldMapping) {
        return "?";
    }

    /** 构建sql占位符 */
    public String buildSql$$(String sql) {
        return sql;
    }

    public String buildSelectKeyWhere(TableMapping tableMapping, String tableName) {
        return tableMapping.getSelectByKeySql().computeIfAbsent(
                tableName,
                k -> buildSql$$(buildSelect(tableMapping, tableName) + " where " + buildKeyWhere(tableMapping))
        );
    }

    public String buildSelect(TableMapping tableMapping, String tableName) {
        return tableMapping.getSelectSql().computeIfAbsent(
                tableName,
                k -> buildSql$$("select * from `" + tableName + "`")
        );
    }

    /** 根据主键列构建where */
    public String buildKeyWhere(TableMapping tableMapping) {
        String sql = "";
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getKeyFields()) {
            if (!sql.isEmpty()) {
                sql += " and ";
            }
            sql += "`" + fieldMapping.getColumnName() + "`" + "=" + build$$(fieldMapping);
        }
        sql = buildSql$$(sql);
        return sql;
    }

    /** insert into */
    public String buildExitSql(Entity bean) {
        TableMapping tableMapping = tableMapping(bean.getClass());
        final String tableName = TableMapping.beanTableName(bean);
        return tableMapping.getExitSql().computeIfAbsent(
                tableName,
                k -> {
                    String sql = "select 1 as 'exits' from `" + tableName + "`";
                    String where = buildKeyWhere(tableMapping);
                    sql += " where " + where;
                    sql = buildSql$$(sql);
                    return sql;
                }
        );
    }

    /** insert into */
    public String buildInsert(TableMapping tableMapping, String tableName) {
        return tableMapping.getInsertSql().computeIfAbsent(
                tableName,
                k -> {
                    String sql = "insert into ";
                    sql += "`" + tableName + "`" + "(";
                    for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
                        sql += "`" + fieldMapping.getColumnName() + "`" + ",";
                    }
                    sql = sql.substring(0, sql.length() - 1);
                    sql += ") values (";
                    for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
                        sql += build$$(fieldMapping) + ",";
                    }
                    sql = sql.substring(0, sql.length() - 1);
                    sql += ")";
                    sql = buildSql$$(sql);
                    return sql;
                }
        );
    }

    public String buildUpdate(TableMapping tableMapping, String tableName) {
        return tableMapping.getUpdateSql().computeIfAbsent(
                tableName,
                k -> {
                    String sql = "update `" + tableName + "` set ";
                    for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
                        if (fieldMapping.isKey()) continue;
                        sql += "`" + fieldMapping.getColumnName() + "`" + "=" + build$$(fieldMapping) + ",";
                    }
                    sql = sql.substring(0, sql.length() - 1);
                    sql += " where ";
                    for (TableMapping.FieldMapping fieldMapping : tableMapping.getKeyFields()) {
                        sql += "`" + fieldMapping.getColumnName() + "`" + "=" + build$$(fieldMapping) + " and ";
                    }
                    sql = sql.substring(0, sql.length() - 5);
                    sql = buildSql$$(sql);
                    return sql;
                }
        );
    }

    public Object[] buildKeyParams(TableMapping tableMapping, Object bean) {
        List<Object> params = new ArrayList<>();
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getKeyFields()) {
            params.add(fieldMapping.toDbValue(bean));
        }
        return params.toArray();
    }

    public Object[] buildInsertParams(TableMapping tableMapping, Object bean) {
        List<Object> params = new ArrayList<>();
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
            params.add(fieldMapping.toDbValue(bean));
        }
        return params.toArray();
    }

    public Object[] builderUpdateParams(TableMapping tableMapping, Object bean) {
        List<Object> params = new ArrayList<>();
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getColumns().values()) {
            if (fieldMapping.isKey()) continue;
            params.add(fieldMapping.toDbValue(bean));
        }
        for (TableMapping.FieldMapping fieldMapping : tableMapping.getKeyFields()) {
            params.add(fieldMapping.toDbValue(bean));
        }
        return params.toArray();
    }

    /** 把数据库的数据转化成对象 */
    public <R> R data2Object(TableMapping tableMapping, JSONObject data) {
        Entity object = tableMapping.newInstance();
        data2Object(tableMapping, object, data);
        object.setNewEntity(false);
        return (R) object;
    }

    public void data2Object(TableMapping tableMapping, Object bean, JSONObject data) {
        LinkedHashMap<String, TableMapping.FieldMapping> columns = tableMapping.getColumns();
        for (Map.Entry<String, TableMapping.FieldMapping> entry : columns.entrySet()) {
            TableMapping.FieldMapping fieldMapping = entry.getValue();
            Object columnValue = fromDbValue(fieldMapping, data);
            if (columnValue != null) {
                fieldMapping.setValue(bean, columnValue);
            }
        }
    }

    public Object fromDbValue(TableMapping.FieldMapping fieldMapping, JSONObject data) {
        Object object = data.get(fieldMapping.getColumnName());
        if (object == null) {
            return null;
        }
        if (fieldMapping.getFileType().isAssignableFrom(object.getClass())) {
            return object;
        }
        switch (fieldMapping.getColumnType()) {
            case Bool -> {
                return Boolean.parseBoolean(object.toString());
            }
            case Int -> {
                if (AtomicInteger.class.isAssignableFrom(fieldMapping.getFileType())) {
                    return new AtomicInteger(Integer.parseInt(object.toString()));
                }
                return Integer.parseInt(object.toString());
            }
            case Long -> {
                if (AtomicLong.class.isAssignableFrom(fieldMapping.getFileType())) {
                    return new AtomicLong(Long.parseLong(object.toString()));
                }
                return Long.parseLong(object.toString());
            }
            case Double -> {
                return Double.parseDouble(object.toString());
            }
            case Float -> {
                return Float.parseFloat(object.toString());
            }
            case Blob -> {
                return FastJsonUtil.parse((byte[]) object, fieldMapping.getJsonType());
            }
            case null, default -> {
                return FastJsonUtil.parse(object.toString(), fieldMapping.getJsonType());
            }
        }
    }

}
