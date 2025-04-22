package wxdgaming.game.test.script.task;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.test.bean.role.Player;
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

    @Init
    public void init(RunApplication runApplication) {
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

    public void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        targetValue = replace(player, k1, k2, k3, targetValue);
        for (ITaskScript taskScript : taskScriptImplHashMap.values()) {
            taskScript.update(player, k1, k2, k3, targetValue);
        }
    }

    long replace(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        return targetValue;
    }

}
