package wxdgaming.game.server.script.validation.impl;

import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.validation.AbstractValidationHandler;
import wxdgaming.game.server.script.validation.ConditionType;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class LevelValidationHandler extends AbstractValidationHandler {

    @Override public ConditionType conditionType() {
        return ConditionType.Level;
    }

    @Override public String tips() {
        return "等级不足";
    }

    @Override public boolean validate(Player player, long[] conditions) {
        if (conditions[1] <= player.getLevel() && player.getLevel() <= conditions[2]) {
            return true;
        }
        return false;
    }

}
