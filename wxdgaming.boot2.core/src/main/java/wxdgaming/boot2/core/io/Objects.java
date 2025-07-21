package wxdgaming.boot2.core.io;


import wxdgaming.boot2.core.Throw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据对象合并
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-07-29 10:33
 */
public class Objects {

    public static final Object[] ZERO_ARRAY = new Object[0];

    public static boolean compare(Object o1, Object o2) {
        long hashCode1 = getHashCode(o1);
        long hashCode2 = getHashCode(o2);

        return hashCode1 == hashCode2;
    }

    public static long getHashCode(Object object) {
        long hashcode = 0;
        try {
            Class<?> clazz = object.getClass();// 根据类名获得其对应的Class对象 写上你想要的类名就是了 注意是全名 如果有包的话要加上 比如java.Lang.String
            Field[] fields = clazz.getDeclaredFields();// 根据Class对象获得属性 私有的也可以获得
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isTransient(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                    //                    System.out.println(" 类：" + clazz.getName() + " 字段：" + field.getName() + " is transient or static or final;");
                    continue;
                }
                Object get = field.get(object);
                if (get != null) {
                    hashcode += java.util.Objects.hashCode(get);
                }
                //                System.out.println(field.getName() + " " + field.getType().getName() + "," + get);//打印每个属性的类型名字
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return hashcode;
    }

    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                oos.flush();
                return baos.toByteArray();
            }
        } catch (Throwable ex) {
            throw Throw.of("对象转 byte[] 出现错误", ex);
        }
    }

    /**
     * @param buf
     * @return
     */
    public static <R> R toObject(byte[] buf) {
        if (buf == null || buf.length < 1) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buf)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (R) ois.readObject();
            }
        } catch (Throwable ex) {
            throw Throw.of("byte[] 转 对象 出现错误", ex);
        }
    }

    /**
     * 深拷贝 效率,高于Fastjson以及BeanUtils
     *
     * @param <R>
     * @param obj
     * @return
     */
    public static <R> R deepCopy(Object obj) {
        return (R) toObject(toBytes(obj));
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }


    /**
     * 合并value
     * <p>如果存在key，就map.get(key) + value 存入 map中
     * <p>如果不存在key，value 存入 map中
     */
    public static int mergeValue(Map<Integer, Integer> map, int key, int mergeValue) {
        return map.merge(key, mergeValue, Math::addExact);
    }

    /**
     * 合并value
     * <p>如果存在key，就map.get(key) + value 存入 map中
     * <p>如果不存在key，value 存入 map中
     */
    public static long mergeValue(Map<Integer, Long> map, int key, long mergeValue) {
        return map.merge(key, mergeValue, Math::addExact);
    }


    /**
     * 数组追加合并
     *
     * @param ts  源数组
     * @param ats 待合并数组
     * @param <T> 对象
     * @return 返回新数组
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-03-20 14:45
     */
    public static <T> T[] merge(T[] ts, T... ats) {
        T[] ts1 = Arrays.copyOf(ts, ts.length + ats.length);
        if (ts1.length - ts.length >= 0)
            System.arraycopy(ats, 0, ts1, ts.length, ats.length);
        return ts1;
    }

    /**
     * 合并数组
     *
     * @param <T>    对象
     * @param first  源数组
     * @param rest   待合并数组
     * @param length 合并数组长度
     * @return 新数组
     */
    public static <T> T[] merge(T[] first, T[] rest, int length) {
        int totalLength = first.length + length;
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(first.getClass().getComponentType(), totalLength);
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(rest, 0, result, first.length, length);
        return result;
    }

    /**
     * 合并数组
     *
     * @param first  源数组
     * @param rest   待合并数组
     * @param length 合并数组长度
     * @return 新数组
     */
    public static byte[] mergeBytes(byte[] first, byte[] rest, int length) {
        int totalLength = first.length + length;
        byte[] result = new byte[totalLength];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(rest, 0, result, first.length, length);
        return result;
    }

    /** 数组追加合并 */
    public static int[] merge(int[] ts, int... ats) {
        int[] ts1 = Arrays.copyOf(ts, ts.length + ats.length);
        for (int i = ts.length; i < ts1.length; i++) {
            ts1[i] = ats[i - ts.length];
        }
        return ts1;
    }

    /** 数组追加合并 */
    public static long[] merge(long[] ts, long... ats) {
        long[] ts1 = Arrays.copyOf(ts, ts.length + ats.length);
        for (int i = ts.length; i < ts1.length; i++) {
            ts1[i] = ats[i - ts.length];
        }
        return ts1;
    }

    /** 合并两个 Map，支持嵌套结构 */
    public static Map<String, Object> mergeMapsNew(Map<String, Object> target, Map<String, Object> source) {
        Map<String, Object> mergeMaps = new HashMap<>();
        mergeMaps(mergeMaps, target);
        mergeMaps(mergeMaps, source);
        return target;
    }

    /** 合并两个 Map，支持嵌套结构 */
    @SuppressWarnings("unchecked")
    public static void mergeMaps(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map && target.containsKey(key) && target.get(key) instanceof Map) {
                // 递归合并嵌套 Map
                mergeMaps((Map<String, Object>) target.get(key), (Map<String, Object>) value);
            } else {
                // 覆盖或添加键值
                target.put(key, value);
            }
        }
    }

    /** 检查数组是否包含某个值 */
    public static boolean checkHave(int[] ints, int value) {
        for (int i : ints) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    /** 检查数组是否包含某个值 */
    public static boolean checkHave(long[] longs, long value) {
        for (long i : longs) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean nonNull(Object obj) {
        return obj != null;
    }

    public static boolean nullEmpty(Object obj) {
        return switch (obj) {
            case null -> true;
            case String string -> string.isBlank();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }

    public static boolean nonNullEmpty(Object obj) {
        return !nullEmpty(obj);
    }

    /** 返回非null的那个参数，优先第一个 */
    @SafeVarargs public static <R> R returnNonNull(R... args) {
        for (int i = 0; i < args.length; i++) {
            R arg = args[i];
            if (arg != null) {
                return arg;
            }
        }
        throw new NullPointerException();
    }

    /** 反转数组 */
    public static int[] reverse(int[] ints) {
        int[] ints1 = new int[ints.length];
        for (int i = 0; i < ints.length; i++) {
            ints1[i] = ints[ints.length - i - 1];
        }
        return ints1;
    }

    public static String toString(Object... args) {
        return Arrays.stream(args)
                .map(a -> {
                    if (a instanceof Object[] os) {
                        return Objects.toString(os);
                    }
                    return String.valueOf(a);
                })
                .collect(Collectors.joining(" "));
    }

}
