package wxdgaming.boot2.starter.excel.store;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.format.StreamWriter;
import wxdgaming.boot2.core.io.FileReadUtil;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.ComboKey;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.ReflectClassProvider;
import wxdgaming.boot2.core.reflect.ReflectFieldProvider;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.ConvertUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

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
    final ReflectClassProvider reflectClassProvider;
    private List<E> dataList;
    private Map<Object, E> dataMap;
    private Map<ComboKey, List<E>> indexesDataMap;

    public DataTable() {
        this.tClass = ReflectProvider.getTClass(this.getClass());
        this.reflectClassProvider = ReflectClassProvider.build(tClass);
        this.dataMapping = AnnUtil.ann(tClass, DataMapping.class, true);
    }

    public void loadJson(String jsonPath) {
        if (!jsonPath.endsWith("/")) {
            jsonPath += "/";
        }
        jsonPath += dataMapping.name() + ".json";
        String json = FileReadUtil.readString(jsonPath);
        AssertUtil.isTrue(StringUtils.isNotBlank(json), "加载配置表：" + this.getClass().getSimpleName() + " 查询文件失败：" + jsonPath);
        setModelList(FastJsonUtil.parseArray(json, tClass));
    }

    public void setModelList(List<E> modelList) {
        if (modelList == null || modelList.isEmpty()) {
            dataList = Collections.emptyList();
            dataMap = Collections.emptyMap();
            return;
        }
        /*不可变列表*/
        final Map<Object, E> modeMap = new LinkedHashMap<>();
        final Map<ComboKey, List<E>> tmpIndexesDataMap = new LinkedHashMap<>();
        modelList.forEach((dbModel) -> {
            try {
                Object keyValue = dbModel.key();
                if (modeMap.put(keyValue, dbModel) != null) {
                    throw new IllegalArgumentException("数据 主键 【" + keyValue + "】 重复");
                }
                Stream<Index> indexesStream = AnnUtil.annStream(DataTable.this.getClass(), Index.class);
                indexesStream.forEach(index -> {
                    String[] ks = index.value();
                    Object[] kvs = new Object[ks.length];
                    for (int i = 0; i < ks.length; i++) {
                        String k = ks[i];
                        kvs[i] = reflectClassProvider.getFieldContext(k).getInvoke(dbModel);
                    }
                    ComboKey comboKey = new ComboKey(kvs);
                    /*添加自定义索引*/
                    List<E> es = tmpIndexesDataMap.computeIfAbsent(comboKey, k -> new ArrayList<>());
                    if (index.single()) {
                        if (!es.isEmpty())
                            throw new IllegalArgumentException("数据 自定义索引 【" + comboKey + "】 【" + keyValue + "】 不唯一");
                    }
                    es.add(dbModel);
                });
            } catch (Throwable e) {
                throw Throw.of("数据：" + FastJsonUtil.toJSONString(dbModel), e);
            }
        });
        /*不可变的列表*/
        this.dataList = Collections.unmodifiableList(modelList);
        this.dataMap = Collections.unmodifiableMap(modeMap);
        this.indexesDataMap = Collections.unmodifiableMap(tmpIndexesDataMap);
        this.initDb();
    }

    public boolean containsByKey(Object key) {
        return dataMap.containsKey(key);
    }

    /**
     * 根据key值获取参数
     *
     * @param key 多条件 key
     */
    public E getByKey(Object key) {
        return dataMap.get(key);
    }

    public boolean containsByIndex(Object... indexes) {
        return indexesDataMap.containsKey(new ComboKey(indexes));
    }

    public List<E> getByIndex(Object... indexes) {
        return indexesDataMap.get(new ComboKey(indexes));
    }

    public E getSingleByIndex(Object... indexes) {
        List<E> es = indexesDataMap.get(new ComboKey(indexes));
        if (es == null || es.isEmpty()) return null;
        return es.getFirst();
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

        streamWriter.writeRight("-", len * reflectClassProvider.getFieldMap().size(), '-').writeLn();
        for (String columnName : reflectClassProvider.getFieldMap().keySet()) {
            streamWriter.write("|").writeRight(columnName, len, ' ');
        }

        streamWriter.writeLn();

        streamWriter.writeRight("-", len * reflectClassProvider.getFieldMap().size(), '-').writeLn();
        for (E row : dataList) {
            streamWriter.writeLn();
            for (ReflectFieldProvider entityField : reflectClassProvider.getFieldMap().values()) {
                Object value = entityField.getInvoke(row);
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
