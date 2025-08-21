package code;

import com.alibaba.fastjson.JSON;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FastUtilTest {

    static int count = 65535;

    static {
        Data2Size.totalSize0(FastUtilTest.class.hashCode());
    }

    @Test
    public void f1() {
        Int2IntOpenHashMap map = new Int2IntOpenHashMap();
        map.put(1, 1);
        int merge = map.merge(1, 1, Math::addExact);
        map.addTo(1, 100);
        System.out.println(merge);
        String jsonString = JSON.toJSONString(map);
        System.out.println(jsonString);
        Int2IntOpenHashMap map1 = JSON.parseObject(jsonString, Int2IntOpenHashMap.class);
        System.out.println(map1);
        Map<Integer, Integer> integerIntegerMap = Collections.unmodifiableMap(map);
    }

    @RepeatedTest(10)
    public void f2() {

        {
            DiffTime diffTime = new DiffTime();
            Int2IntOpenHashMap map = new Int2IntOpenHashMap(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            HashMap<Integer, Integer> map = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            DiffTime diffTime = new DiffTime();
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    @RepeatedTest(10)
    public void f3() {

        {
            DiffTime diffTime = new DiffTime();
            HashMap<Integer, Integer> map = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            AtomicInteger count2 = new AtomicInteger();
            for (Map.Entry<Integer, Integer> integerEntry : map.entrySet()) {
                count2.addAndGet(integerEntry.getValue());
            }
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            Int2IntOpenHashMap map = new Int2IntOpenHashMap(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            AtomicInteger count2 = new AtomicInteger();
            for (Map.Entry<Integer, Integer> integerEntry : map.int2IntEntrySet()) {
                count2.addAndGet(integerEntry.getValue());
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diffMs5() + " ms");

            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    @RepeatedTest(10)
    public void f4() {

        {
            DiffTime diffTime = new DiffTime();
            HashMap<Integer, Object> map = new HashMap<>(16);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (Map.Entry<Integer, Object> integerEntry : map.entrySet()) {
                count2.addAndGet(integerEntry.getKey());
            }
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            Int2ObjectOpenHashMap<Object> map = new Int2ObjectOpenHashMap<>(16);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (Map.Entry<Integer, Object> integerEntry : map.int2ObjectEntrySet()) {
                count2.addAndGet(integerEntry.getKey());
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    /** 测试get性能 */
    @RepeatedTest(10)
    public void f5() {
        {
            DiffTime diffTime = new DiffTime();
            HashMap<Integer, Object> map = new HashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            Int2ObjectOpenHashMap<Object> map = new Int2ObjectOpenHashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    /** 测试get性能 */
    @RepeatedTest(10)
    public void f6() {
        {
            HashMap<Integer, Object> map = new HashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            DiffTime diffTime = new DiffTime();
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            Int2ObjectOpenHashMap<Object> map = new Int2ObjectOpenHashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            DiffTime diffTime = new DiffTime();
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    HashMap<Long, CacheHolder<String>>[] hashMap = new HashMap[]{new HashMap<>(16)};
    Long2ObjectOpenHashMap<CacheHolder<String>>[] objectOpenHashMap = new Long2ObjectOpenHashMap[]{new Long2ObjectOpenHashMap<>(16)};

    /** 测试get性能 */
    @Test
    public void f7() {
        hashMap = new HashMap[]{new HashMap<>(16)};
        objectOpenHashMap = new Long2ObjectOpenHashMap[]{new Long2ObjectOpenHashMap<>(16)};
        {

            for (int i = 0; i < count; i++) {
                hashMap[0].computeIfAbsent((long) i, l -> new CacheHolder<>(String.valueOf(l)));
            }
            DiffTime diffTime = new DiffTime();
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = hashMap[0].get((long) i);
                if (object != null)
                    count2.addAndGet(i);
            }
            System.out.println("HashMap - " + hashMap[0].size() + " - " + diffTime.diffMs5() + " ms");
            System.out.println(Data2Size.totalSizes0(hashMap[0]));
        }
        System.out.println("-----------------------------------------------");
        {

            for (int i = 0; i < count; i++) {
                objectOpenHashMap[0].computeIfAbsent((long) i, l -> new CacheHolder<>(String.valueOf(l)));
            }
            DiffTime diffTime = new DiffTime();
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = objectOpenHashMap[0].apply(i);
                if (object != null)
                    count2.addAndGet(i);
            }
            System.out.println("Int2IntOpenHashMap - " + objectOpenHashMap[0].size() + " - " + diffTime.diffMs5() + " ms");
            System.out.println(Data2Size.totalSizes0(objectOpenHashMap[0]));
        }
        System.out.println("================================================");
    }

    @Getter
    protected static class CacheHolder<V> {

        private final V value;
        /** 最后执行心跳的时间 */

        @Setter private long lastHeartTime = System.currentTimeMillis();
        /** 过期时间 */
        @Setter private long expireEndTime = System.currentTimeMillis();

        public CacheHolder(V value) {
            this.value = value;
        }

        @Override public String toString() {
            return JSON.toJSONString(value);
        }
    }

}
