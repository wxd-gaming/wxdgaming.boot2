package wxdgaming.game.server.script.validation.impl;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.validation.AbstractValidationHandler;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationType;
import wxdgaming.game.server.bean.role.Player;

/**
 * 等级验证 条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class JobValidationHandler extends AbstractValidationHandler<Player> {

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
