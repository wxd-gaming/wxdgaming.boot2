package wxdgaming.game.server.script.condition;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.condition.AbstractConditionProcessor;
import wxdgaming.boot2.starter.condition.Condition;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.condition.ServerConditionDTO;

/**
 * 开服天数处理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-26 19:33
 **/
@Component
public class OpenDayConditionProcessor extends AbstractConditionProcessor<ServerConditionDTO> {

    final GameServerProperties gameServerProperties;

    public OpenDayConditionProcessor(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
    }

    @Override public String conditionKey() {
        return "OpenDay";
    }

    @Override public String tips(ServerConditionDTO self, Condition condition) {
        return "开服天数不足" + condition.targetValue();
    }

    @Override public long selfValue(ServerConditionDTO self, Condition condition) {
        return gameServerProperties.openDay();
    }
}
