package wxdgaming.game.test.script.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.UpdateType;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.task.init.ConditionInitValueHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:43
 **/
@Singleton
public class TaskModuleScript extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private HashMap<Integer, ITaskScript> taskScriptImplHashMap = new HashMap<>();
    private HashMap<Condition, ConditionInitValueHandler> conditionInitValueHandlerMap = new HashMap<>();

    @Inject
    public TaskModuleScript(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

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

    public TaskPack getTaskPack(Player player) {
        return dataCenterService.taskPack(player.getUid());
    }

    public Condition getCondition(int id) {
        return new Condition(1, 1, 1, UpdateType.Add);
    }

    public ITaskScript getTaskScript(int taskType) {
        ITaskScript taskScript = taskScriptImplHashMap.get(taskType);
        AssertUtil.assertNull(taskScript, "任务类型不存在：" + taskType);
        return taskScript;
    }

    /** 初始化任务的时候调用变量初始化，比如接取任务的初始化当前等级 */
    public void initTask(Player player, TaskInfo taskInfo, Map<Integer, Long> conditions) {
        for (Map.Entry<Integer, Long> entry : conditions.entrySet()) {
            Integer conditionId = entry.getKey();
            Condition condition = getCondition(conditionId);
            ConditionInitValueHandler conditionInitValueHandler = conditionInitValueHandlerMap.get(condition);
            if (conditionInitValueHandler != null) {
                long initValue = conditionInitValueHandler.initValue(player, condition);
                taskInfo.getProgresses().put(conditionId, initValue);
            }
        }
    }

    public void update(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        TaskPack taskPack = getTaskPack(player);
        targetValue = replace(player, k1, k2, k3, targetValue);
        List<TaskInfo> changes = new ArrayList<>();
        for (ITaskScript taskScript : taskScriptImplHashMap.values()) {
            taskScript.update(player, taskPack, changes, k1, k2, k3, targetValue);
        }
        /* TODO发送变更列表 */
        for (TaskInfo taskInfo : changes) {

        }
    }

    long replace(Player player, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        return targetValue;
    }

}
