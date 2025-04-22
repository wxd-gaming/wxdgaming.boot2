package wxdgaming.game.test.script.task;

import com.google.inject.Singleton;
import wxdgaming.game.test.bean.role.Player;

import java.io.Serializable;

/**
 * 任务模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:43
 **/
@Singleton
public class TaskModuleScript {

    public void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        targetValue = replace(player, k1, k2, k3, targetValue);
    }

    long replace(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        return targetValue;
    }

}
