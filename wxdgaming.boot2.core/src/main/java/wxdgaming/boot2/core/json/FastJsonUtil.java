package wxdgaming.boot2.core.json;

import com.alibaba.fastjson2.*;
import wxdgaming.boot2.core.function.SLFunction1;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-04-21 10:09
 **/
public class FastJsonUtil {

    public static final class AutoTypeBeforeHandlerImpl implements JSONReader.AutoTypeBeforeHandler {

        private static final AutoTypeBeforeHandlerImpl INSTANCE = new AutoTypeBeforeHandlerImpl();

        public static AutoTypeBeforeHandlerImpl getInstance() {
            return INSTANCE;
        }

        public final HashSet<String> PACKAGE_PREFIX = new HashSet<>();

        private AutoTypeBeforeHandlerImpl() {
            PACKAGE_PREFIX.add(Set.class.getPackageName());
            PACKAGE_PREFIX.add(List.class.getPackageName());
            PACKAGE_PREFIX.add(Map.class.getPackageName());
            PACKAGE_PREFIX.add("wxdgaming.");
        }

        @Override
        public Class<?> apply(String typeName, Class<?> expectClass, long features) {
            if (PACKAGE_PREFIX.stream().anyMatch(typeName::startsWith)) {
                try {
                    return Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new JSONException("Class not found: " + typeName);
                }
            }
            throw new JSONException("Unauthorized class: " + typeName);
        }
    }


    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringKeyAsString(Object object) {
        return toJSONString(object, JSONWriter.Feature.WriteNonStringKeyAsString);
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringAllAsString(Object object) {
        return toJSONString(
                object,
                JSONWriter.Feature.WriteNonStringKeyAsString,
                JSONWriter.Feature.WriteNonStringValueAsString
        );
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringWriteTypeKeyAsString(Object object) {
        return toJSONString(object,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.WriteNonStringKeyAsString
        );
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringWriteTypeAllAsString(Object object) {
        return toJSONString(
                object,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.WriteNonStringKeyAsString,
                JSONWriter.Feature.WriteNonStringValueAsString
        );
    }

    /** 一般是js用的，所有 key 值都是字符串 格式化 */
    public static String toJSONStringFmtKeyAsString(Object object) {
        return toJSONString(
                object,
                JSONWriter.Feature.PrettyFormat,
                JSONWriter.Feature.WriteNonStringKeyAsString
        );
    }

    /** 一般是js用的，所有 key - value 值都是字符串 格式化 */
    public static String toJSONStringFmtAllAsString(Object object) {
        return toJSONString(
                object,
                JSONWriter.Feature.PrettyFormat,
                JSONWriter.Feature.WriteNonStringKeyAsString,
                JSONWriter.Feature.WriteNonStringValueAsString
        );
    }

    /** 格式化 */
    public static String toJSONString(Object object, JSONWriter.Feature... features) {
        return JSON.toJSONString(object, features);
    }

    /** 格式化 ,包含数据类型 {@code @class} */
    public static String toJSONStringAsWriteType(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.WriteClassName);
    }

    /** 格式化 */
    public static String toJSONStringAsFmt(Object object) {
        return JSON.toJSONString(object,JSONWriter.Feature.WriteNonStringValueAsString, JSONWriter.Feature.PrettyFormat);
    }

    /** 格式化,包含数据类型 {@code @class} */
    public static String toJsonFmtWriteType(Object object) {
        return JSON.toJSONString(object, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.PrettyFormat);
    }

    /** 格式化,包含数据类型 {@code @class} */
    public static byte[] toJSONBytesAsWriteType(Object object) {
        return JSON.toJSONBytes(object, JSONWriter.Feature.WriteClassName);
    }

    /** 转化成字节流 */
    public static byte[] toJSONBytes(Object object, JSONWriter.Feature... features) {
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
        return JSON.parseObject(bytes, JSONObject.class);
    }

    public static <T> T parse(byte[] bytes, Type clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    public static <T> T parse(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    /** 自带类型推断 */
    public static <T> T parseSupportAutoType(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz, AutoTypeBeforeHandlerImpl.getInstance());
    }

    /** 自带类型推断 */
    public static <T> T parseSupportAutoType(byte[] bytes, Type clazz) {
        return JSON.parseObject(bytes, clazz, AutoTypeBeforeHandlerImpl.getInstance());
    }

    public static <T, F> T parse(String str, SLFunction1<F, ?> function) {
        Type type = ParameterizedTypeImpl.genericFieldTypes(function);
        return JSON.parseObject(str, type);
    }

    public static JSONObject parse(String str, JSONReader.Feature... features) {
        return JSON.parseObject(str, JSONObject.class, features);
    }

    public static <T> T parse(String str, Type type, JSONReader.Feature... features) {
        return JSON.parseObject(str, type, features);
    }

    public static <T> T parse(String str, Class<T> clazz, JSONReader.Feature... features) {
        return JSON.parseObject(str, clazz, features);
    }

    public static <T> T parse(String str, TypeReference<T> tTypeReference, JSONReader.Feature... features) {
        return JSON.parseObject(str, tTypeReference, features);
    }

    /** 反序列化会使用自动类型推动 */
    public static <T> T parseSupportAutoType(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz, AutoTypeBeforeHandlerImpl.getInstance());
    }

    /** 反序列化会使用自动类型推动 */
    public static <T> T parseSupportAutoType(String str, Type clazz) {
        return JSON.parseObject(str, clazz, AutoTypeBeforeHandlerImpl.getInstance());
    }

    /** 反序列化会使用自动类型推动 */
    public static <T> T parseSupportAutoType(String str, TypeReference<T> tTypeReference) {
        return JSON.parseObject(str, tTypeReference, AutoTypeBeforeHandlerImpl.getInstance());
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
    public static <T> T parseObject(Object object, Type type, Supplier<Object> supplier) {
        if (object == null) {
            if (supplier == null) return null;
            Object defaultValue = supplier.get();
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

    public static <T> T getObject(JSONObject source, String key, Type type, Supplier<Object> supplier) {
        T object = source.getObject(key, type);
        return parseObject(object, type, supplier);
    }

    public static <T> T getNestedValue(JSONObject source, String path, Type clazz) {
        return getNestedValue(source, path, clazz, null);
    }

    /** 泛型方法：通过路由获取嵌套的 JSON 数据并转换为指定类型 */
    public static <T> T getNestedValue(JSONObject source, String path, Type type, Supplier<Object> supplier) {
        Object value = getNestedValue(source, path);
        return parseObject(value, type, supplier);
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
