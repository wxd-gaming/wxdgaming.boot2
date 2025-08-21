package code;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.io.Objects;

import java.util.HashMap;
import java.util.Map;

public class MapMergeTest {


    @Test
    public void t() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        System.out.println(map.merge(1, 1, Math::addExact));
        System.out.println(map.merge(1, 1, Math::addExact));
    }

    @Test
    public void mergeMap() {
        Map<String, Object> map1 = new HashMap<>();

        map1.put("a", 1);
        map1.put("b", new JSONObject().fluentPut("1", "1"));
        map1.put("c", new JSONObject().fluentPut("1", new JSONObject().fluentPut("2", "2")));

        Map<String, Object> map2 = new HashMap<>();

        map2.put("a", 1);
        map2.put("b", new JSONObject().fluentPut("2", "2"));
        map2.put("c", new JSONObject().fluentPut("2", new JSONObject().fluentPut("1", "1")));

        Map<String, Object> map3 = Objects.mergeMapsNew(map1, map2);
        System.out.println(FastJsonUtil.toJSONStringAsFmt(map3));

    }

}
