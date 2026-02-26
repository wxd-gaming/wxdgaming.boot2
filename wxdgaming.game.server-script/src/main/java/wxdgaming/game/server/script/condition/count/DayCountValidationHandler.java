package wxdgaming.game.server.script.condition.count;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.condition.Condition;
import wxdgaming.game.server.bean.condition.ServerConditionDTO;
import wxdgaming.game.server.bean.count.CountValidationType;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class DayCountValidationHandler extends AbstractCountValidationHandler {

    public CountValidationType getValidationType() {
        return CountValidationType.DayCount;
    }

    @Override public String tips(ServerConditionDTO self, Condition condition) {
        return "每日次数已达上限";
    }

}
