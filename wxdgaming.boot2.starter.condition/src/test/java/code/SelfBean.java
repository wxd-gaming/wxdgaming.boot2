package code;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.condition.ConditionDTO;

/**
 * 参数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 17:32
 **/
@Getter
@Setter
public class SelfBean extends ConditionDTO {

    private TestBean player;

    @Override public String toString() {
        return "SelfBean{" +
               "player=" + player +
               '}';
    }

}
