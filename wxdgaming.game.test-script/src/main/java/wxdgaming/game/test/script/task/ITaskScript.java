package wxdgaming.game.test.script.task;

import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.event.OnLogin;

import java.io.Serializable;

public abstract class ITaskScript {

    protected RunApplication runApplication;
    protected PgsqlService pgsqlService;
    protected TaskModuleScript taskModuleScript;

    @Init
    public void init(RunApplication runApplication, PgsqlService pgsqlService, TaskModuleScript taskModuleScript) {
        this.runApplication = runApplication;
        this.pgsqlService = pgsqlService;
        this.taskModuleScript = taskModuleScript;
    }

    public abstract int type();

    public TaskPack getTaskPack(Player player) {
        return taskModuleScript.getTaskPack(player);
    }

    @OnLogin public void onLogin(Player player) {
        TaskPack taskPack = getTaskPack(player);
        initTask(player, taskPack);
    }

    /** 初始化 */
    public abstract void initTask(Player player, TaskPack taskPack);

    /** 接受任务 */
    public abstract void acceptTask(Player player, TaskPack taskPack, int taskId);

    /** 更新 */
    public abstract void update(Player player, TaskPack taskPack, Serializable k1, Serializable k2, Serializable k3, long targetValue);

    /** 提交任务 */
    public abstract void submitTask(Player player, TaskPack taskPack, int taskId);

}
