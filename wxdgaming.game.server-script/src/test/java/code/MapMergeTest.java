package code;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapMergeTest {


    @Test
    public void t() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        System.out.println(map.merge(1, 1, Math::addExact));
        System.out.println(map.merge(1, 1, Math::addExact));
    }

}
