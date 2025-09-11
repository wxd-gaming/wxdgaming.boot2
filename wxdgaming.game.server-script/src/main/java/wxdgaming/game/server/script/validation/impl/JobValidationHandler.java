package wxdgaming.game.server.script.validation.impl;

import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.validation.AbstractValidationHandler;
import wxdgaming.game.server.script.validation.Validation;
import wxdgaming.game.server.script.validation.ValidationType;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class JobValidationHandler extends AbstractValidationHandler {

    @Override public ValidationType conditionType() {
        return ValidationType.Job;
    }

    @Override public String tips() {
        return "职业不匹配";
    }

    @Override public boolean validate(Player player, Validation validation) {
        return validation.test(player.getJob());
    }

}
