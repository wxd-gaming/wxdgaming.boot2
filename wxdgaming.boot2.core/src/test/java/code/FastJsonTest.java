package code;

import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.collection.ListOf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-05 11:20
 **/
public class FastJsonTest {

    @Test
    public void t0() {
        List<Object> objects = Collections.emptyList();
        String jsonString = FastJsonUtil.toJSONStringAsWriteType(objects);
        List parse = FastJsonUtil.parse(jsonString, List.class);
        System.out.println(parse.getClass().getName());
    }

    @Test
    public void j2() {

        String json = "2D";
        Object parse = FastJsonUtil.parse(json, new TypeReference<>() {});
        System.out.println(parse);
        System.out.println(parse.getClass().getName());

        setPop("test", 1D);
        setPopDouble("d1", 1);
        System.out.println(FastJsonUtil.toJSONString(popMap));
    }

    public HashMap<String, Object> popMap = new HashMap<>();

    public void setPopDouble(String key, double value) {
        popMap.put(key, value);
    }

    public void setPop(String key, Object value) {
        popMap.put(key, value);
    }

}
