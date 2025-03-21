package code;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

}
