package wxdgaming.game.server.script.validation;

import wxdgaming.game.server.bean.role.Player;

/**
 * 验证
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 10:53
 **/
public abstract class AbstractValidationHandler {

    public abstract ConditionType conditionType();

    public abstract String tips();

    public abstract boolean validate(Player player, long[] conditions);

}
