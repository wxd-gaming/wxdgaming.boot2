package code;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Int2ObjectOpenHashMapTest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class A {
        private String name;
        private int age;
    }

    A a = new A("a", 3);
    final Map<Integer, A> map = new HashMap<>();

    @Test
    public void test() {
        map.put(1, new A("s", 1));
        String jsonString = FastJsonUtil.toJSONStringAsWriteType(this);
        System.out.println(jsonString);
        Int2ObjectOpenHashMapTest map1 = FastJsonUtil.parseSupportAutoType(jsonString, this.getClass());
        System.out.println(JSON.toJSONString(map1, FastJsonUtil.Writer_Features_Type_Name));
    }
}
