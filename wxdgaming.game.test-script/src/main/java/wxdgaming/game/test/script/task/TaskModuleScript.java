package wxdgaming.game.test.script.task;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.task.init.ConditionInitValueHandler;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 任务模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:43
 **/
@Singleton
public class TaskModuleScript {

    private HashMap<Integer, ITaskScript> taskScriptImplHashMap = new HashMap<>();
    private HashMap<Condition, ConditionInitValueHandler> conditionInitValueHandlerMap = new HashMap<>();
    protected RunApplication runApplication;
    protected PgsqlService pgsqlService;

    @Init
    public void init(RunApplication runApplication, PgsqlService pgsqlService) {
        this.runApplication = runApplication;
        this.pgsqlService = pgsqlService;
        HashMap<Condition, ConditionInitValueHandler> tmpConditionInitValueHandlerMap = new HashMap<>();
        runApplication.classWithSuper(ConditionInitValueHandler.class)
                .forEach(conditionInitValueHandler -> {
                    if (tmpConditionInitValueHandlerMap.put(conditionInitValueHandler.condition(), conditionInitValueHandler) != null) {
                        throw new RuntimeException("重复的任务条件处理器：" + conditionInitValueHandler.condition());
                    }
                });
        conditionInitValueHandlerMap = tmpConditionInitValueHandlerMap;

        HashMap<Integer, ITaskScript> tmpTaskScriptImplHashMap = new HashMap<>();
        runApplication.classWithSuper(ITaskScript.class)
                .forEach(taskScript -> {
                    if (taskScriptImplHashMap.put(taskScript.type(), taskScript) != null) {
                        throw new RuntimeException("任务类型处理重复：" + taskScript.type());
                    }
                });
        taskScriptImplHashMap = tmpTaskScriptImplHashMap;
    }

    public TaskPack getTaskPack(Player player) {
        return pgsqlService.getCacheService().cache(TaskPack.class, player.getUid());
    }

    public ITaskScript getTaskScript(int taskType) {
        ITaskScript taskScript = taskScriptImplHashMap.get(taskType);
        AssertUtil.assertNull(taskScript, "任务类型不存在：" + taskType);
        return taskScript;
    }

    public void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        TaskPack taskPack = getTaskPack(player);
        targetValue = replace(player, k1, k2, k3, targetValue);
        for (ITaskScript taskScript : taskScriptImplHashMap.values()) {
            taskScript.update(player, taskPack, k1, k2, k3, targetValue);
        }
    }

    long replace(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        return targetValue;
    }

}
