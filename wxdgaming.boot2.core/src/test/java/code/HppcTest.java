package code;

import com.alibaba.fastjson.JSON;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FastUtilTest {

    static int count = 500000;

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
    }

    @Test
    @RepeatedTest(10)
    public void f2() {
        Data2Size.totalSize0(this.hashCode());
        {
            DiffTime diffTime = new DiffTime();
            Int2IntOpenHashMap map = new Int2IntOpenHashMap(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            System.out.println("Int2IntOpenHashMap - " + diffTime.diff() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            HashMap<Integer, Integer> map = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            DiffTime diffTime = new DiffTime();
            System.out.println("HashMap - " + diffTime.diff() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    @Test
    @RepeatedTest(10)
    public void f3() {
        Data2Size.totalSize0(this.hashCode());
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
            System.out.println("HashMap - " + diffTime.diff() + " ms");
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
            System.out.println("Int2IntOpenHashMap - " + diffTime.diff() + " ms");

            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    @Test
    @RepeatedTest(10)
    public void f4() {
        Data2Size.totalSize0(this.hashCode());
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
            System.out.println("HashMap - " + diffTime.diff() + " ms");
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
            System.out.println("Int2IntOpenHashMap - " + diffTime.diff() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    /** 测试get性能 */
    @Test
    @RepeatedTest(10)
    public void f5() {
        Data2Size.totalSize0(this.hashCode());
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
            System.out.println("HashMap - " + diffTime.diff() + " ms");
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
            System.out.println("Int2IntOpenHashMap - " + diffTime.diff() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

    /** 测试get性能 */
    @Test
    @RepeatedTest(10)
    public void f6() {
        Data2Size.totalSize0(this.hashCode());
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
            System.out.println("HashMap - " + diffTime.diff() + " ms");
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
            System.out.println("Int2IntOpenHashMap - " + diffTime.diff() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

}
