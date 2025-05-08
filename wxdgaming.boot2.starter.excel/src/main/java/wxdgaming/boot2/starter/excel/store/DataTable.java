package wxdgaming.boot2.starter.excel.store;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.format.StreamWriter;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.FieldUtil;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.util.ConvertUtil;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据模型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-09 10:08
 **/
@Getter
public abstract class DataTable<E extends DataKey> extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;
    final Class<E> tClass;
    final DataMapping dataMapping;
    final Map<String, Field> fieldMap;
    private List<E> dataList;
    private Map<Object, E> dataMap;

    public DataTable() {
        tClass = ReflectContext.getTClass(this.getClass());
        fieldMap = FieldUtil.getFields(false, tClass);
        dataMapping = AnnUtil.ann(tClass, DataMapping.class, true);
    }

    public DataTable<E> loadJson(String jsonPath) {
        if (!jsonPath.endsWith("/")) {
            jsonPath += "/";
        }
        jsonPath += dataMapping.name() + ".json";
        String json = FileReadUtil.readString(jsonPath);
        return setModelList(FastJsonUtil.parseArray(json, tClass));
    }

    public DataTable<E> setModelList(List<E> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            dataList = List.of();
            dataMap = Map.of();
            return this;
        }
        /*不可变列表*/
        final Map<Object, E> modeMap = new LinkedHashMap<>();
        modelList.forEach((dbModel) -> {
            try {
                Object keyValue = dbModel.key();
                if (modeMap.put(keyValue, dbModel) != null) {
                    throw new RuntimeException("数据 主键 【" + keyValue + "】 重复");
                }
                if (dbModel instanceof DataChecked dataChecked) {
                    dataChecked.initAndCheck();
                }
                Keys keys = AnnUtil.ann(DataTable.this.getClass(), Keys.class);
                if (keys != null) {
                    for (String s : keys.value()) {
                        String index = "";
                        String[] split = s.split(keys.split());
                        for (String filedName : split) {
                            Object fv = fieldMap.get(filedName).get(dbModel);
                            if (!index.isEmpty()) index += keys.split();
                            index += fv;
                        }
                        /*添加自定义索引*/
                        if (modeMap.put(index, dbModel) != null) {
                            throw new Throw("数据 自定义索引 【" + s + "】 【" + keyValue + "】 重复 ");
                        }
                    }
                }
            } catch (Throwable e) {
                throw Throw.of("数据：" + FastJsonUtil.toJSONString(dbModel), e);
            }
        });
        /*不可变的列表*/
        this.dataList = Collections.unmodifiableList(modelList);
        this.dataMap = Collections.unmodifiableMap(modeMap);
        return this;
    }

    /**
     * 根据key值获取参数
     *
     * @param key
     * @return
     */
    public E get(Object key) {
        return dataMap.get(key);
    }

    public boolean containsKey(Object key) {
        return dataMap.containsKey(key);
    }

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public int dbSize() {
        return this.dataList.size();
    }

    /** 初始化，做一些构建相关的操作 */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public void initDb() {
    }

    /** 检查数据合法性 */
    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public void checkData(Map<Class<?>, DataTable<?>> store) {
    }

    public String toDataString() {
        return toDataString(50);
    }

    public String toDataString(int len) {
        StreamWriter streamWriter = new StreamWriter();
        toDataString(streamWriter, len);
        return streamWriter.toString();
    }

    public void toDataString(StreamWriter streamWriter) {
        toDataString(streamWriter, 50);
    }

    public void toDataString(StreamWriter streamWriter, int len) {
        streamWriter.write("解析：").write(tClass.getName()).write("\n");
        streamWriter.write("表名：").write(dataMapping.name()).write("\n");

        streamWriter.writeRight("-", len * fieldMap.size(), '-').write("\n");
        for (String columnName : fieldMap.keySet()) {
            streamWriter.write("|").writeRight(columnName, len, ' ');
        }

        streamWriter.write("\n");

        streamWriter.writeRight("-", len * fieldMap.size(), '-').write("\n");
        for (E row : dataList) {
            streamWriter.write("\n");
            for (Field entityField : fieldMap.values()) {
                entityField.setAccessible(true);
                Object value = null;
                try {
                    value = entityField.get(row);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (value == null) {
                    streamWriter.write("|").writeRight("-", len, ' ');
                } else if (ConvertUtil.isBaseType(value.getClass())) {
                    streamWriter.write("|").writeRight(String.valueOf(value), len, ' ');
                } else {
                    streamWriter.write("|").writeRight(FastJsonUtil.toJSONString(value), len, ' ');
                }
            }
        }
        streamWriter.write("\n");
    }

}
