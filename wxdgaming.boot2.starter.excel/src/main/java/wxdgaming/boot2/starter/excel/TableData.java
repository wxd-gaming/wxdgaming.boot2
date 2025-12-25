package wxdgaming.boot2.starter.excel;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.format.TableFormatter;
import wxdgaming.boot2.core.util.PatternUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.util.ConvertUtil;

import java.util.Map;
import java.util.Optional;

/**
 * excel sheet 数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-08 20:49
 **/
@Getter
@Accessors(chain = true)
public class TableData {

    /** 对应的excel文件 */
    private final String filePath;
    /** 文件名 */
    private final String fileName;
    /** 标签 */
    private final String sheetName;
    /** 表名字 */
    private final String tableName;
    /** 表注释 */
    private final String tableComment;
    /** 表头 */
    Map<Integer, CellInfo> cellInfo4IndexMap;
    /** 行数据 */
    Map<Object, RowData> rows;

    public TableData(String filePath, String fileName,
                     String sheetName, String tableName, String tableComment) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.tableName = tableName;
        this.tableComment = tableComment;
    }

    public Optional<RowData> row(Object key) {
        return Optional.ofNullable(rows.get(key));
    }

    /** 生成代码文件的名字 */
    @JSONField(serialize = false, deserialize = false)
    public String getCodeClassName() {

        String[] split = tableName.split("_|-");
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                split[i] = PatternUtil.capitalize(split[i]);
            }
        }
        String codeName = String.join("", split);
        return PatternUtil.capitalize(codeName);
    }

    @JSONField(serialize = false, deserialize = false)
    public String getString(Object key, String field) {
        RowData rowData = rows.get(key);
        if (rowData == null) {
            return null;
        }
        return rowData.getString(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean getBooleanValue(Object key, String field) {
        RowData rowData = rows.get(key);
        if (rowData == null) {
            return false;
        }
        return rowData.getBooleanValue(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public Boolean getBoolean(Object key, String field) {
        RowData rowData = rows.get(key);
        if (rowData == null) {
            return null;
        }
        return rowData.getBoolean(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public int getIntValue(Object key, String field) {
        RowData rowData = rows.get(key);
        if (rowData == null) {
            return 0;
        }
        return rowData.getIntValue(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public Integer getInteger(Object key, String field) {
        RowData rowData = rows.get(key);
        if (rowData == null) {
            return null;
        }
        return rowData.getInteger(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public long getLongValue(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return 0;
        }
        return jsonObject.getLongValue(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public Long getLong(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getLong(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public float getFloatValue(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return 0.0f;
        }
        return jsonObject.getFloatValue(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public Float getFloat(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getFloat(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public ConfigString getConfigString(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return null;
        }
        return (ConfigString) jsonObject.get(field);
    }

    @JSONField(serialize = false, deserialize = false)
    public int[] getIntArray(Object key, String field) {
        return getT(key, field, int[].class);
    }

    @JSONField(serialize = false, deserialize = false)
    public int[][] getInt2Array(Object key, String field) {
        return getT(key, field, int[][].class);
    }

    @JSONField(serialize = false, deserialize = false)
    public long[] getLongArray(Object key, String field) {
        return getT(key, field, long[].class);
    }

    @JSONField(serialize = false, deserialize = false)
    public long[][] getLong2Array(Object key, String field) {
        return getT(key, field, long[][].class);
    }

    @JSONField(serialize = false, deserialize = false)
    public <R> R getT(Object key, String field, Class<R> clazz) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getObject(field, clazz);
    }

    @JSONField(serialize = false, deserialize = false)
    public Object getObject(Object key, String field) {
        RowData jsonObject = rows.get(key);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(field);
    }

    /** 把所有的数据，转化成json字符串 */
    public String data2Json() {
        Object array = rows.values().stream().toList();
        return FastJsonUtil.toJSONStringAsFmt(array);
    }

    /** 把所有的数据，转化成json字符串 */
    public String data2Lua() {
        StringBuilder builder = new StringBuilder();
        builder.append("""
                --- %s %s
                --- %s %s
                
                --- @class %s
                """.formatted(tableName, tableComment, filePath, sheetName, getCodeClassName()));

        for (CellInfo cellInfo : cellInfo4IndexMap.values()) {
            builder.append("---@field ").append(cellInfo.getFieldName()).append(" any ").append(cellInfo.getFieldComment()).append("\n");
        }

        builder.append("""
                %s = {}
                %s.__index = %s
                
                ---@type table<string, %s>
                %sTable = {
                """.formatted(getCodeClassName(), getCodeClassName(), getCodeClassName(), getCodeClassName(), getCodeClassName())
        );

        boolean appendDouhao1 = false;
        for (Map.Entry<Object, RowData> entry : rows.entrySet()) {
            if (appendDouhao1) {
                builder.append(",\n");
            }
            Object uid = entry.getKey();
            builder.append("[%s] = {".formatted((uid instanceof Number) ? uid.toString() : "\"" + uid.toString() + "\""));
            boolean appendDouhao2 = false;
            for (Map.Entry<String, Object> fieldEntry : entry.getValue().entrySet()) {
                if (appendDouhao2) {
                    builder.append(", ");
                }
                builder.append(fieldEntry.getKey()).append(" = ");
                Object value = fieldEntry.getValue();
                if (value == null) {
                    builder.append("nil");
                } else {
                    if (value instanceof Number || value instanceof Boolean) {
                        builder.append(value);
                    } else {
                        builder.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
                    }
                }
                appendDouhao2 = true;
            }
            builder.append(" } ");
            appendDouhao1 = true;
        }
        builder.append("\n}");
        builder.append("""
                
                
                ---@param id string id
                ---@return %s 道具配置
                function %sTable.get(id)
                    local cfg = %sTable[id]
                    if (cfg == nil) then
                        return nil
                    end
                    return setmetatable(cfg, %s)
                end
                
                ---@param field string 字段名字
                ---@param value any 字段值
                ---@return %s 道具配置
                function %sTable.find(field, value)
                    for _, v in pairs(%sTable) do
                        if (v[field] == value) then
                            return setmetatable(v, %s)
                        end
                    end
                    return nil
                end
                
                """.formatted(
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName(),
                getCodeClassName()
        ));
        return builder.toString();
    }


    public String showData() {
        TableFormatter tableFormatter = new TableFormatter();
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldBelong).toArray();
            tableFormatter.addRow( array);
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldName).toArray();
            tableFormatter.addRow( array);
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(v -> v.getFieldType().getSimpleName()).toArray();
            tableFormatter.addRow( array);
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getCellType).toArray();
            tableFormatter.addRow( array);
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldComment).toArray();
            tableFormatter.addRow( array);
        }
        {
            for (JSONObject row : rows.values()) {
                Object[] array = row.values().stream().map(value -> {
                    if (value == null) {
                        return "-";
                    } else if (ConvertUtil.isBaseType(value.getClass())) {
                        return String.valueOf(value);
                    } else {
                        return FastJsonUtil.toJSONString(value);
                    }
                }).toArray();
                tableFormatter.addRow( array);
            }
        }
        String s = tableFormatter.generateTable();

        return """
                解析：%s
                表名：%s
                %s
                """.formatted(this.getTableName(), this.getTableComment(), s);
    }

    @Override public String toString() {
        return "TableInfo{tableComment='%s', tableName='%s', sheetName='%s', fileName='%s', filePath='%s'}"
                .formatted(tableComment, tableName, sheetName, fileName, filePath);
    }
}
