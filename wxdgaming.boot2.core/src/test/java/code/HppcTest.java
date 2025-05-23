package code;

import com.alibaba.fastjson.JSON;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HppcTest {

    static int count = 500000;

    @Test
    public void f1() {
        IntIntHashMap map = new IntIntHashMap();
        map.put(1, 1);
        map.addTo(1, 100);
        String jsonString = JSON.toJSONString(map);
        System.out.println(jsonString);
        IntIntHashMap map1 = JSON.parseObject(jsonString, IntIntHashMap.class);
        System.out.println(map1);
    }

    @Test
    @RepeatedTest(10)
    public void f2() {
        Data2Size.totalSize0(this.hashCode());
        {
            DiffTime diffTime = new DiffTime();
            IntIntHashMap map = new IntIntHashMap(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            System.out.println("IntIntHashMap - " + diffTime.diffMs5() + " ms");
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
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            IntIntHashMap map = new IntIntHashMap(count);
            for (int i = 0; i < count; i++) {
                map.put(i, 1);
            }
            AtomicInteger count2 = new AtomicInteger();

            for (IntIntCursor cursor : map) {
                count2.addAndGet(cursor.value);
            }
            System.out.println("IntIntHashMap - " + diffTime.diffMs5() + " ms");

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
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            IntObjectHashMap<Object> map = new IntObjectHashMap<>(16);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (IntObjectCursor<Object> cursor : map) {
                count2.addAndGet(cursor.key);
            }
            System.out.println("IntObjectHashMap - " + diffTime.diffMs5() + " ms");
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
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            DiffTime diffTime = new DiffTime();
            IntObjectHashMap<Object> map = new IntObjectHashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("IntObjectHashMap - " + diffTime.diffMs5() + " ms");
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
            System.out.println("HashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("-----------------------------------------------");
        {
            IntObjectHashMap<Object> map = new IntObjectHashMap<>(count + 100);
            for (int i = 0; i < count; i++) {
                map.put(i, String.valueOf(i));
            }
            DiffTime diffTime = new DiffTime();
            AtomicInteger count2 = new AtomicInteger();
            for (int i = 0; i < count; i++) {
                Object object = map.get(i);
                if (object != null) count2.incrementAndGet();
            }
            System.out.println("IntObjectHashMap - " + diffTime.diffMs5() + " ms");
            // System.out.println(Data2Size.totalSizes0(map));
        }
        System.out.println("================================================");
    }

}
