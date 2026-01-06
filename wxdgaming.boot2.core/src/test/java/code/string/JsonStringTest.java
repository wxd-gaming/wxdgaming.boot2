package code.string;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-11 09:09
 **/
public class JsonStringTest {

    @Test
    public void t1(){
        String json= """
                {"a": 1,
                "b" : "ddfd,dgd,gd,gdg,d,gdg,",
                "c" : 3}
                """;
        System.out.println(JSON.parseObject(json));
        System.out.println(JSON.parseObject(json).getString("b"));
    }

}
