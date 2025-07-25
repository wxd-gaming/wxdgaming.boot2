package wxdgaming.game.server.script.task.init.handler;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.ConditionUpdatePolicyConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.task.init.ConditionInitValueHandler;

/**
 * 初始化任务的时候获取等级
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:55
 **/
@Singleton
public class LevelHandler implements ConditionInitValueHandler {

    @Override public Condition condition() {
        return new Condition("Lv", ConditionUpdatePolicyConst.Replace, 0);
    }

    @Override public long initValue(Player player, Condition condition) {
        return player.getLevel();
    }

}
