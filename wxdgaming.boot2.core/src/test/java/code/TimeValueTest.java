package code;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.TimeValue;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-18 20:23
 **/
public class TimeValueTest {

    @Test
    public void t1() {
        LV1 lv1 = new LV1();
        String jsonString = FastJsonUtil.toJSONString(lv1);
        System.out.println(jsonString);
        System.out.println(FastJsonUtil.parse(jsonString, LV1.class));
        System.out.println(FastJsonUtil.parse("{\"createTime\":1742302895306}", LV1.class));
        System.out.println(FastJsonUtil.parse("{\"createTime\":\"2025/03/18 11:02:58\"}", LV1.class));
        System.out.println(FastJsonUtil.parse("{\"createTime\":\"2025/03/18 11:02\"}", LV1.class));
        System.out.println(FastJsonUtil.parse("{\"createTime\":\"2025/03/19 11\"}", LV1.class));
        System.out.println(FastJsonUtil.parse("{\"createTime\":\"2025/03/19\"}", LV1.class));
    }

    @Getter
    @Setter
    static class LV1 {
        private TimeValue createTime = new TimeValue(System.currentTimeMillis());

        @Override public String toString() {
            return "LV1{" +
                   "createTime=" + createTime +
                   '}';
        }
    }
}
