package wxdgaming.game.server.script.validation.impl;

import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.validation.AbstractValidationHandler;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationType;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.bean.role.Player;

/**
 * 开服第几天
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:55
 **/
@Component
public class OpenDayValidationHandler extends AbstractValidationHandler<Player> {

    final GameServerProperties gameServerProperties;

    public OpenDayValidationHandler(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
    }

    @Override public ValidationType conditionType() {
        return ValidationType.OpenDay;
    }

    @Override public String tips() {
        return "开服天数不满足";
    }

    @Override public boolean validate(Player player, Validation validation) {
        int target = gameServerProperties.openDay();
        return validation.test(target);
    }

}
