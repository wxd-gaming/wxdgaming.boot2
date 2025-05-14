package wxdgaming.game.test.script.task.replace.handler;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.UpdateType;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.task.replace.ConditionReplaceValueHandler;

/**
 * 初始化任务的时候获取等级
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:55
 **/
@Singleton
public class LevelHandler implements ConditionReplaceValueHandler {

    @Override public Condition condition() {
        return new Condition("level", "0", "0", UpdateType.Replace);
    }

    @Override public long replaceValue(Player player, Condition condition) {
        return player.getLevel();
    }

}
