package wxdgaming.boot2.starter.excel.store;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.Throw;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.format.StreamWriter;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.lang.AssertException;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.FieldUtil;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.AssertUtil;
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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-10-09 10:08
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
        tClass = ReflectProvider.getTClass(this.getClass());
        fieldMap = FieldUtil.getFields(false, tClass);
        dataMapping = AnnUtil.ann(tClass, DataMapping.class, true);
    }

    public void loadJson(String jsonPath) {
        if (!jsonPath.endsWith("/")) {
            jsonPath += "/";
        }
        jsonPath += dataMapping.name() + ".json";
        String json = FileReadUtil.readString(jsonPath);
        AssertUtil.assertTrue(StringUtils.isNotBlank(json), "加载配置表：" + this.getClass().getSimpleName() + " 查询文件失败：" + jsonPath);
        setModelList(FastJsonUtil.parseArray(json, tClass));
    }

    public void setModelList(List<E> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            dataList = List.of();
            dataMap = Map.of();
            return;
        }
        /*不可变列表*/
        final Map<Object, E> modeMap = new LinkedHashMap<>();
        modelList.forEach((dbModel) -> {
            try {
                Object keyValue = dbModel.key();
                if (modeMap.put(keyValue, dbModel) != null) {
                    throw new AssertException("数据 主键 【" + keyValue + "】 重复");
                }
                Keys keys = AnnUtil.ann(DataTable.this.getClass(), Keys.class);
                if (keys != null) {
                    for (String s : keys.value()) {
                        StringBuilder index = new StringBuilder();
                        String[] split = s.split(keys.split());
                        for (String filedName : split) {
                            Object fv = fieldMap.get(filedName).get(dbModel);
                            if (!index.isEmpty()) index.append(keys.split());
                            index.append(fv);
                        }
                        /*添加自定义索引*/
                        if (modeMap.put(index.toString(), dbModel) != null) {
                            throw new AssertException("数据 自定义索引 【" + s + "】 【" + keyValue + "】 重复 ");
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
        this.initDb();
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

    @JSONField(serialize = false, deserialize = false)
    public int dbSize() {
        return this.dataList.size();
    }

    /** 初始化，做一些构建相关的操作 */
    @JSONField(serialize = false, deserialize = false)
    public void initDb() {
    }

    /** 检查数据合法性 */
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
        streamWriter.write("解析：").write(tClass.getName()).writeLn();
        streamWriter.write("表名：").write(dataMapping.name()).writeLn();

        streamWriter.writeRight("-", len * fieldMap.size(), '-').writeLn();
        for (String columnName : fieldMap.keySet()) {
            streamWriter.write("|").writeRight(columnName, len, ' ');
        }

        streamWriter.writeLn();

        streamWriter.writeRight("-", len * fieldMap.size(), '-').writeLn();
        for (E row : dataList) {
            streamWriter.writeLn();
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
        streamWriter.writeLn();
    }

}
