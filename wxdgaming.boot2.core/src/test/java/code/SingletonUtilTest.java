package code;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.util.SingletonUtil;

import java.util.HashMap;

@Getter
@Setter
@SpringBootTest(classes = {CoreScan.class})
public class SingletonUtilTest {

    HashMap<Object, Object> map = new HashMap<>();
    HashMap<Object, Object> map2 = new HashMap<>();
    HashMap<String, String> map3 = new HashMap<>();

    public void log(String marker) {
        System.out.println("%s map  %s".formatted(marker, Data2Size.totalSizes0(this.map)));
        System.out.println("%s map2 %s".formatted(marker, Data2Size.totalSizes0(this.map2)));
        System.out.println("%s map3 %s".formatted(marker, Data2Size.totalSizes0(this.map2)));
        System.out.println("%s      %s".formatted(marker, Data2Size.totalSizes0(this)));
        String jsonString = FastJsonUtil.toJSONString(this);
        SingletonUtilTest parse = FastJsonUtil.parse(jsonString, this.getClass());
        System.out.println("%s      %s".formatted(marker, Data2Size.totalSizes0(parse)));
        System.out.println("----------------------------------------------");
    }

    @Test
    public void s1() {
        for (long l = 1000; l < 100000; l++) {
            map.put(l, l);
            map2.put(l, l);
            map3.put(l + "", l + "");
        }
        log("s1");
    }

    @Test
    public void s2() {
        for (long l = 1000; l < 100000; l++) {
            Long singleton = SingletonUtil.singleton(l);
            map.put(l, l);
            map2.put(singleton, singleton);
            map3.put(singleton.toString(), singleton.toString());
        }
        log("s2");
    }

    @Test
    public void s3() {
        for (long l = 1000; l < 100000; l++) {
            Long singleton = SingletonUtil.singleton(l);
            map.put(singleton, singleton);
            map2.put(singleton, singleton);
            String string = singleton.toString();
            string = SingletonUtil.singleton(string);
            map3.put(string, string);
        }
        log("s3");
    }

}
