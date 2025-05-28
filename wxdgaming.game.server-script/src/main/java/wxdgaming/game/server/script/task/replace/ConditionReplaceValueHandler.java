package wxdgaming.game.server.script.task.replace;

import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.server.bean.role.Player;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:50
 **/
public interface ConditionReplaceValueHandler {

    Condition condition();

    long replaceValue(Player player, Condition condition);

}
