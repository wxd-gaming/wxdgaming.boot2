package code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapCopyOfTest {

    @Test
    public void c1() {
        LinkedHashMap<Integer, Integer> map1 = new LinkedHashMap<>();
        map1.put(3, 3);
        map1.put(1, 1);
        map1.put(4, 4);
        map1.put(2, 2);
        map1.put(5, 5);
        System.out.println(map1);
        System.out.println(Collections.unmodifiableMap(map1));
        System.out.println(Map.copyOf(map1));
    }

    @Test
    public void m0() {

        ConcurrentHashMap<Integer, MapCopyOfTest> map1 = new ConcurrentHashMap<>();

        MapCopyOfTest mapCopyOfTest = map1.computeIfAbsent(1, l -> {
            return new MapCopyOfTest();
        });

    }

    @Test
    public void m1() {


        ConcurrentHashMap<Integer, Integer> map1 = new ConcurrentHashMap<>();
        {
            Integer compute = map1.compute(1, (k, v) -> v == null ? 1 : v + 1);
            System.out.println(compute);
        }
        {
            Integer compute = map1.compute(1, (k, v) -> v == null ? 1 : v + 1);
            System.out.println(compute);
        }

    }

    @Test
    public void tojson() {
        A1 a1 = new A1();
        a1.setA1(1000);
        a1.setAcc(2000);
        String jsonString = JSON.toJSONString(a1);
        System.out.println(a1.toString() + " " + jsonString);
        B1 b1 = JSON.parseObject(jsonString, B1.class);
        System.out.println(b1.toString());
    }

    @Getter
    @Setter
    public static class A1 {

        private int a1;
        private int acc;

        @Override public String toString() {
            return "A1{a1=%d, acc=%d}".formatted(a1, acc);
        }
    }

    @Getter
    @Setter
    public static class B1 {
        private int a1;
        @JSONField(name = "acc")
        private int bcc;

        @Override public String toString() {
            return "B1{a1=%d, bcc=%d}".formatted(a1, bcc);
        }
    }

}
