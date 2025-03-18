package wxdgaming.boot2.starter.excel;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.util.ConvertUtil;

import java.util.Map;
import java.util.Optional;

/**
 * excel sheet 数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 20:49
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
    @JSONField(serialize = true)
    public String getCodeClassName() {

        String[] split = tableName.split("_|-");
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                split[i] = StringUtils.upperFirst(split[i]);
            }
        }
        String codeName = String.join("", split);
        return StringUtils.upperFirst(codeName);
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

    /**
     * 把所有的数据，转化成json字符串
     *
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-10 14:05
     */
    public String data2Json() {
        Object array = rows.values().stream().toList();
        return FastJsonUtil.toJSONStringAsFmt(array);
    }

    public String showData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getTableName()).append("\n");
        stringBuilder.append(this.getTableComment()).append("\n");
        String format = "";
        for (int i = 0; i < cellInfo4IndexMap.size(); i++) {
            format += "|%-40s\t";
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldBelong).toArray();
            String formatted = String.format(format, array);
            stringBuilder.append(formatted).append("\n");
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldName).toArray();
            String formatted = String.format(format, array);
            stringBuilder.append(formatted).append("\n");
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(v -> v.getFieldType().getSimpleName()).toArray();
            String formatted = String.format(format, array);
            stringBuilder.append(formatted).append("\n");
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getCellType).toArray();
            String formatted = String.format(format, array);
            stringBuilder.append(formatted).append("\n");
        }
        {
            Object[] array = cellInfo4IndexMap.values().stream().map(CellInfo::getFieldComment).toArray();
            String formatted = String.format(format, array);
            stringBuilder.append(formatted).append("\n");
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
                String formatted = String.format(format, array);
                stringBuilder.append(formatted).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Override public String toString() {
        return "TableInfo{" +
               "tableComment='" + tableComment + '\'' +
               ", tableName='" + tableName + '\'' +
               ", sheetName='" + sheetName + '\'' +
               ", fileName='" + fileName + '\'' +
               ", filePath='" + filePath + '\'' +
               '}';
    }
}
