package wxdgaming.boot2.starter.lua;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaTableValue;
import party.iroiro.luajava.value.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-01 09:39
 */
public class LuaUtils {

    public static Object luaValue2Object(LuaValue luaValue) {
        if (luaValue.type() == Lua.LuaType.NUMBER) {
            if (luaValue instanceof LuaLong) {
                long int64 = luaValue.toInteger();
                int int32 = (int) int64;
                if (int64 == int32) {
                    return int32;
                } else {
                    return int64;
                }
            }
            return luaValue.toNumber();
        } else if (luaValue.type() == Lua.LuaType.TABLE) {
            LuaTableValue luaTableValue = (LuaTableValue) luaValue;
            Map<Object, Object> map = new HashMap<>();
            for (Map.Entry<LuaValue, LuaValue> entry : luaTableValue.entrySet()) {
                map.put(luaValue2Object(entry.getKey()), luaValue2Object(entry.getValue()));
            }
            return map;
        } else if (luaValue.type() == Lua.LuaType.NONE || luaValue.type() == Lua.LuaType.NIL) {
            return null;
        } else if (luaValue.type() == Lua.LuaType.FUNCTION) {
            return "lua method";
        }
        return luaValue.toJavaObject();
    }

    public static void push(Lua L, Object object) {
        if (object != null && object.getClass().isArray()) {
            L.pushJavaArray(object);
        } else {
            L.push(object, Lua.Conversion.FULL);
        }
    }

    // 通用转换方法
    private static <K, V> Map<K, V> convertMap(Object object,
                                               Function<Object, K> keyConverter,
                                               Function<Object, V> valueConverter) {
        if (object == null) return null;

        Map<K, V> result = new HashMap<>();
        if (object instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                K key = keyConverter.apply(entry.getKey());
                V value = valueConverter.apply(entry.getValue());
                result.put(key, value);
            }
        } else {
            throw new RuntimeException("Invalid type: " + object.getClass());
        }
        return result;
    }

    // 通用列表转换方法
    private static <K, V> List<Map<K, V>> convertListMap(Object object,
                                                         Function<Object, K> keyConverter,
                                                         Function<Object, V> valueConverter) {
        if (object == null) return null;

        List<Map<K, V>> list = new ArrayList<>();
        if (object instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Map<?, ?> valueMap) {
                    Map<K, V> convertedMap = convertMap(valueMap, keyConverter, valueConverter);
                    list.add(convertedMap);
                } else {
                    throw new RuntimeException("Invalid type: " + value.getClass());
                }
            }
        } else {
            throw new RuntimeException("Invalid type: " + object.getClass());
        }
        return list;
    }

    // 数字转换器
    private static int toInt(Object obj) {
        if (obj instanceof Number number) {
            return number.intValue();
        } else if (obj instanceof String string) {
            return Integer.parseInt(string);
        }
        throw new RuntimeException("Invalid key type: " + obj.getClass());
    }

    private static long toLong(Object obj) {
        if (obj instanceof Number number) {
            return number.longValue();
        } else if (obj instanceof String string) {
            return Long.parseLong(string);
        }
        throw new RuntimeException("Invalid key type: " + obj.getClass());
    }

    // 简化后的转换方法
    public static Map<Integer, Integer> object2MapIntInt(Object object) {
        return convertMap(object, LuaUtils::toInt, LuaUtils::toInt);
    }

    public static Map<Integer, Long> object2MapIntLong(Object object) {
        return convertMap(object, LuaUtils::toInt, LuaUtils::toLong);
    }

    public static Map<Long, Integer> object2MapLongInt(Object object) {
        return convertMap(object, LuaUtils::toLong, LuaUtils::toInt);
    }

    public static Map<Long, Long> object2MapLongLong(Object object) {
        return convertMap(object, LuaUtils::toLong, LuaUtils::toLong);
    }

    public static List<Map<Integer, Long>> object2ListMapIntLong(Object object) {
        return convertListMap(object, LuaUtils::toInt, LuaUtils::toLong);
    }

    public static List<Map<Long, Long>> object2ListMapLongLong(Object object) {
        return convertListMap(object, LuaUtils::toLong, LuaUtils::toLong);
    }

    public static List<Map<String, Long>> object2ListMapStringLong(Object object) {
        return convertListMap(object, String::valueOf, LuaUtils::toLong);
    }

    public static List<Map<String, String>> object2ListMapStringString(Object object) {
        return convertListMap(object, String::valueOf, String::valueOf);
    }

    public static Map<String, Object> object2MapStringObject(Object object) {
        return convertMap(object, String::valueOf, obj -> obj);
    }

    public static Map<Object, Object> object2MapObjectObject(Object object) {
        return convertMap(object, obj -> obj, obj -> obj);
    }

}
