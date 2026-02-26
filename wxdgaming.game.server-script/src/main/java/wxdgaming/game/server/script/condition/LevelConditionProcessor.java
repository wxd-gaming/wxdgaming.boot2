package wxdgaming.game.server.script.condition;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.condition.AbstractConditionProcessor;
import wxdgaming.boot2.starter.condition.Condition;
import wxdgaming.game.server.bean.condition.ServerConditionDTO;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class LevelConditionProcessor extends AbstractConditionProcessor<ServerConditionDTO> {

    @Override public String conditionKey() {
        return "level";
    }

    @Override public String tips(ServerConditionDTO self, Condition condition) {
        return "当前等级 %s级 不足 %s级".formatted(selfValue(self, condition), condition.targetValue());
    }

    @Override public long selfValue(ServerConditionDTO self, Condition condition) {
        return self.getPlayer().getLevel();
    }

}
