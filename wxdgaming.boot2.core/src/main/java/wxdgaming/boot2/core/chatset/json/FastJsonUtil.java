package wxdgaming.boot2.core.chatset.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import wxdgaming.boot2.core.collection.EmptyList;
import wxdgaming.boot2.core.collection.EmptyMap;
import wxdgaming.boot2.core.function.SLFunction1;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.ConfigString;
import wxdgaming.boot2.core.lang.TimeValue;
import wxdgaming.boot2.core.lang.bit.BitFlag;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-04-21 10:09
 **/
public class FastJsonUtil {

    /** 默认值 */
    public static final SerializerFeature[] Writer_Features;
    /** 格式化 */
    public static final SerializerFeature[] Writer_Features_Fmt;
    /** 写入字段类型 */
    public static final SerializerFeature[] Writer_Features_Type_Name;
    /** 写入字段类型 */
    public static final SerializerFeature[] Writer_Features_Type_Name_NOT_ROOT;
    /** 写入字段类型，并且格式化 */
    public static final SerializerFeature[] Writer_Features_Type_Name_Fmt;
    /**
     * 这个是会给所有的值对象加入引号，不管是否是字符串；
     * <p>json 标准key值必须是字符串
     */
    public static final SerializerFeature[] Writer_Features_Key_String;
    /**
     * 这个是会给所有的值对象加入引号，不管是否是字符串；
     * <p>json 标准key值必须是字符串, js long精度问题需要转string
     */
    public static final SerializerFeature[] Writer_Features_K_V_String;
    /** 包含类型字段，全部转换成字符串的json */
    public static final SerializerFeature[] Writer_Type_Name_Features_Key_String;
    /** 包含类型字段，全部转换成字符串的json */
    public static final SerializerFeature[] Writer_Type_Name_Features_Value_String;
    /** 包含类型字段，全部转换成字符串的json */
    public static final SerializerFeature[] Writer_Type_Name_Features_K_V_String;
    /**
     * 这个是会给所有的值对象加入引号，不管是否是字符串；
     * <p>json 标准key值必须是字符串
     */
    public static final SerializerFeature[] Writer_Features_Fmt_Key_String;
    /**
     * 这个是会给所有的值对象加入引号，不管是否是字符串；
     * <p>json 标准key值必须是字符串, js long精度问题需要转string
     */
    public static final SerializerFeature[] Writer_Features_Fmt_K_V_String;

    public static final Feature[] Reader_Features;


    static {
        /*fast json 启动类型自动推断*/
        Writer_Features = new SerializerFeature[]{
                SerializerFeature.QuoteFieldNames,                  /*给字段加引号*/
                SerializerFeature.WriteMapNullValue,                /*map字段如果为null,输出为null*/
                SerializerFeature.WriteNullListAsEmpty,             /*List字段如果为null,输出为[],而非null*/
                SerializerFeature.WriteNullNumberAsZero,            /*数值字段如果为null,输出为0,而非null*/
                SerializerFeature.WriteNullBooleanAsFalse,          /*Boolean字段如果为null,输出为false,而非null*/
                SerializerFeature.WriteNullStringAsEmpty,           /*String字段如果为null,输出为"",而非null*/
                SerializerFeature.SkipTransientField,               /*忽律 transient*/
                SerializerFeature.WriteEnumUsingName,               /*枚举用 toString() */
                // SerializerFeature.IgnoreNonFieldGetter,             /*忽略 没有 get 属性 继续写入*/
                SerializerFeature.DisableCircularReferenceDetect,   /*屏蔽循环引用*/
                SerializerFeature.SortField,                        /*排序*/
                SerializerFeature.MapSortField                      /*排序*/
        };

        Reader_Features = new Feature[]{Feature.IgnoreAutoType};

        Writer_Features_Fmt = Objects.merge(Writer_Features, SerializerFeature.PrettyFormat);

        Writer_Features_Type_Name = Objects.merge(Writer_Features, SerializerFeature.WriteClassName);

        Writer_Features_Type_Name_NOT_ROOT = Objects.merge(Writer_Features_Type_Name, SerializerFeature.NotWriteRootClassName);

        Writer_Features_Type_Name_Fmt = Objects.merge(Writer_Features_Type_Name, SerializerFeature.PrettyFormat);

        Writer_Features_Key_String = Objects.merge(Writer_Features, SerializerFeature.WriteNonStringKeyAsString);/*所有的 key 都用引号*/
        Writer_Features_K_V_String = Objects.merge(Writer_Features_Key_String, SerializerFeature.WriteNonStringValueAsString);/*所有的 value 都用引号*/

        Writer_Type_Name_Features_Key_String = Objects.merge(Writer_Features_Type_Name, SerializerFeature.WriteNonStringKeyAsString);/*所有的 key 都用引号*/
        Writer_Type_Name_Features_Value_String = Objects.merge(Writer_Features_Type_Name, SerializerFeature.WriteNonStringValueAsString);/*所有的 key 都用引号*/
        Writer_Type_Name_Features_K_V_String = Objects.merge(Writer_Features_Type_Name, SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.WriteNonStringValueAsString);/*所有的 value 都用引号*/

        Writer_Features_Fmt_Key_String = Objects.merge(Writer_Features_Key_String, SerializerFeature.PrettyFormat);/*所有的 key 都用引号*/
        Writer_Features_Fmt_K_V_String = Objects.merge(Writer_Features_K_V_String, SerializerFeature.PrettyFormat);/*所有的 value 都用引号*/

        ParserConfig.getGlobalInstance().setAutoTypeSupport(false);

        SerializeConfig.getGlobalInstance().put(BitSet.class, BitSetSerializerFastJson.default_instance);
        ParserConfig.getGlobalInstance().putDeserializer(BitSet.class, BitSetSerializerFastJson.default_instance);

        SerializeConfig.getGlobalInstance().put(BitFlag.class, BitFlagSerializerFastJson.default_instance);
        ParserConfig.getGlobalInstance().putDeserializer(BitFlag.class, BitFlagSerializerFastJson.default_instance);

        SerializeConfig.getGlobalInstance().put(TimeValue.class, TimeValueSerializerFastJson.default_instance);
        ParserConfig.getGlobalInstance().putDeserializer(TimeValue.class, TimeValueSerializerFastJson.default_instance);

        SerializeConfig.getGlobalInstance().put(ConfigString.class, ConfigStringSerializerFastJson.default_instance);
        ParserConfig.getGlobalInstance().putDeserializer(ConfigString.class, ConfigStringSerializerFastJson.default_instance);

    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringKeyAsString(Object object) {
        return toJSONString(object, Writer_Features_Key_String);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringAllAsString(Object object) {
        return toJSONString(object, Writer_Features_K_V_String);
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringWriteTypeKeyAsString(Object object) {
        return toJSONString(object, Writer_Type_Name_Features_Key_String);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringWriteTypeAllAsString(Object object) {
        return toJSONString(object, Writer_Type_Name_Features_K_V_String);
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringFmtKeyAsString(Object object) {
        return toJSONString(object, Writer_Features_Fmt_Key_String);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringFmtAllAsString(Object object) {
        return toJSONString(object, Writer_Features_Fmt_K_V_String);
    }

    /** 格式化 */
    public static String toJSONString(Object object) {
        return toJSONString(object, Writer_Features);
    }

    /** 格式化 */
    public static String toJSONString(Object object, SerializerFeature... features) {
        return JSON.toJSONString(object, features);
    }

    /** 格式化 ,包含数据类型 {@code @class} */
    public static String toJSONStringAsWriteType(Object object) {
        return JSON.toJSONString(object, Writer_Features_Type_Name);
    }

    /** 格式化 */
    public static String toJSONStringAsFmt(Object object) {
        return JSON.toJSONString(object, Writer_Features_Fmt);
    }

    /** 格式化,包含数据类型 {@code @class} */
    public static String toJsonFmtWriteType(Object object) {
        return JSON.toJSONString(object, Writer_Features_Type_Name_Fmt);
    }

    /** 格式化,包含数据类型 {@code @class} */
    public static byte[] toJSONBytesAsWriteType(Object object) {
        return JSON.toJSONBytes(object, Writer_Features_Type_Name);
    }

    /** 转化成字节流 */
    public static byte[] toJSONBytes(Object object) {
        return JSON.toJSONBytes(object, Writer_Features);
    }

    /** 转化成字节流 */
    public static byte[] toJSONBytes(Object object, SerializerFeature... features) {
        return JSON.toJSONBytes(object, features);
    }

    public static JSONObject parseJSONObject(Object object) {
        return parse(toJSONString(object));
    }

    /** 通过反射解析某个类的某个字段，然后转化 */
    public static <T, F> T parse(byte[] bytes, SLFunction1<F, ?> function) {
        Type type = ParameterizedTypeImpl.genericFieldTypes(function);
        return parse(bytes, type);
    }

    public static JSONObject parse(byte[] bytes) {
        return JSON.parseObject(bytes, JSONObject.class, Reader_Features);
    }

    public static <T> T parse(byte[] bytes, Type clazz) {
        return JSON.parseObject(bytes, clazz, Reader_Features);
    }

    public static <T> T parse(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz, Reader_Features);
    }

    /** 自带类型推断 */
    public static <T> T parseSupportAutoType(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz, Feature.SupportAutoType);
    }

    /** 自带类型推断 */
    public static <T> T parseSupportAutoType(byte[] bytes, Type clazz) {
        return JSON.parseObject(bytes, clazz, Feature.SupportAutoType);
    }

    public static <T, F> T parse(String str, SLFunction1<F, ?> function) {
        Type type = ParameterizedTypeImpl.genericFieldTypes(function);
        return JSON.parseObject(str, type, Reader_Features);
    }

    public static JSONObject parse(String str) {
        return JSON.parseObject(str, JSONObject.class, Reader_Features);
    }

    public static <T> T parse(String str, Type type) {
        return JSON.parseObject(str, type, Reader_Features);
    }

    public static <T> T parse(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz, Reader_Features);
    }

    public static <T> T parse(String str, TypeReference<T> tTypeReference) {
        return JSON.parseObject(str, tTypeReference, Reader_Features);
    }

    public static <T> T parseSupportAutoType(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz, Feature.SupportAutoType);
    }

    public static <T> T parseSupportAutoType(String str, Type clazz) {
        return JSON.parseObject(str, clazz, Feature.SupportAutoType);
    }

    public static <T> T parseSupportAutoType(String str, TypeReference<T> tTypeReference) {
        return JSON.parseObject(str, tTypeReference, Feature.SupportAutoType);
    }

    /**
     * 多重泛型  数据结构
     * List<R>
     */
    public static JSONArray parseArray(String jsonString) {
        return parse(jsonString, JSONArray.class);
    }

    /**
     * 多重泛型  数据结构
     * List<R>
     */
    public static <R> List<R> parseArray(String jsonString, Type innerClass) {
        return parse(jsonString, ParameterizedTypeImpl.genericTypes(ArrayList.class, ArrayList.class, innerClass));
    }

    /**
     * 多重泛型  数据结构
     * List<R>
     */
    public static <R> List<R> parseArray(byte[] bytes, Type innerClass) {
        return parse(bytes, ParameterizedTypeImpl.genericTypes(ArrayList.class, ArrayList.class, innerClass));
    }

    public static Map<String, String> parseStringMap(String jsonString) {
        return parseMap(jsonString, String.class, String.class);
    }

    public static <K, V> Map<K, V> parseMap(String jsonString, Type keyType, Type valueType) {
        return
                parse(
                        jsonString,
                        ParameterizedTypeImpl.genericTypes(HashMap.class, HashMap.class, keyType, valueType)
                );
    }

    public static Map<String, String> parseStringMap(byte[] bytes) {
        return parseMap(bytes, String.class, String.class);
    }

    public static <K, V> Map<K, V> parseMap(byte[] bytes, Type keyType, Type valueType) {
        return
                parse(
                        bytes,
                        ParameterizedTypeImpl.genericTypes(HashMap.class, HashMap.class, keyType, valueType)
                );
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseObject(Object object, Type type, Object defaultValue) {
        if (object == null) {
            if (defaultValue == null) return null;
            if (type instanceof Class<?> clazz && clazz.isInstance(defaultValue)) {
                return (T) clazz.cast(defaultValue);
            }
            object = FastJsonUtil.parse(String.valueOf(defaultValue), type);
        }
        if (type instanceof Class<?> clazz && clazz.isInstance(object)) {
            return (T) clazz.cast(object);
        }
        return (T) FastJsonUtil.parse(String.valueOf(object), type);
    }

    public static <T> T getObject(JSONObject source, String key, Type type, Object defaultValue) {
        T object = source.getObject(key, type);
        return parseObject(object, type, defaultValue);
    }

    public static <T> T getNestedValue(JSONObject source, String path, Type clazz) {
        return getNestedValue(source, path, clazz, null);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public static <T> T getNestedValue(JSONObject source, String path, Type type, Object defaultValue) {
        Object value = getNestedValue(source, path);
        return parseObject(value, type, defaultValue);
    }

    /** 新增方法：通过路由获取嵌套的 JSON 数据 */
    private static Object getNestedValue(JSONObject source, String path) {
        String[] keys = path.split("\\.");
        Object current = source;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (!(current instanceof JSONObject jsonObject)) {
                return null; // 如果当前对象不是 JSON 对象，则返回 null
            }
            current = jsonObject.get(key);
            if (current == null) {
                return null; // 如果路径中的某个部分不是 JSON 对象，则返回 null
            }
        }
        return current; // 返回最终的 JSON 对象或值
    }

}
