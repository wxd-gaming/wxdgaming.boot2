package wxdgaming.game.test.script.task.impl;

import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.task.ITaskScript;

import java.io.Serializable;

/**
 * 主线任务实现类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 10:46
 **/
public class TaskMainScript implements ITaskScript {

    @Override public int type() {
        return 1;
    }

    /** 初始化 */
    @Override public void initTask(Player player) {

    }

    /** 接受任务 */
    @Override public void acceptTask(Player player) {

    }

    /** 更新 */
    @Override public void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {

    }

    /** 提交任务 */
    @Override public void submitTask(Player player, int taskId) {

    }

}
