package wxdgaming.game.test.script.task;

import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.event.OnLogin;

import java.io.Serializable;

public interface ITaskScript {

    int type();

    @OnLogin default void onLogin(Player player) {
        initTask(player);
    }

    /** 初始化 */
    void initTask(Player player);

    /** 接受任务 */
    void acceptTask(Player player);

    /** 更新 */
    void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue);

    /** 提交任务 */
    void submitTask(Player player, int taskId);

}
