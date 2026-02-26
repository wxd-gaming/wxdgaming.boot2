package wxdgaming.game.server.script.condition.count;

import wxdgaming.boot2.starter.condition.AbstractConditionProcessor;
import wxdgaming.boot2.starter.condition.Condition;
import wxdgaming.game.server.bean.condition.ServerConditionDTO;
import wxdgaming.game.server.bean.count.CountData;
import wxdgaming.game.server.bean.count.CountValidationType;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
public abstract class AbstractCountValidationHandler extends AbstractConditionProcessor<ServerConditionDTO> {

    public abstract CountValidationType getValidationType();

    @Override public String conditionKey() {
        return getValidationType().name();
    }

    @Override public String tips(ServerConditionDTO self, Condition condition) {
        return "次数已达上限";
    }

    @Override public long selfValue(ServerConditionDTO self, Condition condition) {
        CountData countData = self.getCountMap().getValidationMap().computeIfAbsent(getValidationType(), l -> new CountData());
        countData.checkClear(getValidationType().getCheck());
        return countData.getCount();
    }


}
