//package wxdgaming.boot2.core.chatset.json;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.*;
//import wxdgaming.boot2.core.Throw;
//
//import java.lang.reflect.Type;
//
///**
// * jackson
// *
// * @author wxd-gaming(無心道, 15388152619)
// * @version 2025-05-08 19:56
// **/
//public class JacksonUtil {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private static final ObjectMapper mapperFmt = new ObjectMapper();
//
//    static {
//        /*反序列化如果没有setter 则忽略*/
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        // 启用缩进输出（默认使用 2 个空格缩进）
//        mapperFmt.enable(SerializationFeature.INDENT_OUTPUT);
//        mapperFmt.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
//        mapperFmt.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }
//
//    public static byte[] toJSONBytes(Object obj) {
//        try {
//            return mapper.writeValueAsBytes(obj);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static String toJSONString(Object obj) {
//        try {
//            return mapper.writeValueAsString(obj);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static String toJSONStringFmt(Object obj) {
//        try {
//            return mapperFmt.writeValueAsString(obj);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static JsonNode parse(String json) {
//        try {
//            return mapper.readTree(json);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static <T> T parse(byte[] bytes, Class<T> cls) {
//        try {
//            return mapper.readValue(bytes, cls);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static <T> T parse(String json, Class<T> cls) {
//        try {
//            return mapper.readValue(json, cls);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static <T> T parse(String json, TypeReference<T> cls) {
//        try {
//            return mapper.readValue(json, cls);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static <T> T parse(String json, JavaType cls) {
//        try {
//            return mapper.readValue(json, cls);
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//    public static <T> T parse(String json, Type type) {
//        try {
//            return mapper.readValue(json, new TypeReference<T>() {
//                @Override public Type getType() {
//                    return type;
//                }
//            });
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//
//
//    public static <T> T parse(byte[] bytes, Type type) {
//        try {
//            return mapper.readValue(bytes, new TypeReference<T>() {
//                @Override public Type getType() {
//                    return type;
//                }
//            });
//        } catch (Exception e) {
//            throw ExceptionUtils.asRuntimeException(e);
//        }
//    }
//}
