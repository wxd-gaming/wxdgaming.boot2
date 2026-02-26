package code.processor;

import code.SelfBean;
import code.TestBean;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.condition.AbstractConditionProcessor;
import wxdgaming.boot2.starter.condition.Condition;

/**
 * 等级条件处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 16:31
 **/
@Component
public class LevelConditionProcessor extends AbstractConditionProcessor {

    @Override public String conditionKey() {
        return "level";
    }

    @Override public String tips(JSONObject params, Condition condition) {
        return "当前等级 %s级 不足 %s级".formatted(selfValue(params, condition), condition.targetValue());
    }

    @Override public long selfValue(JSONObject params, Condition condition) {
        TestBean player = ((SelfBean) params).getPlayer();
        return player.getLevel();
    }

}
