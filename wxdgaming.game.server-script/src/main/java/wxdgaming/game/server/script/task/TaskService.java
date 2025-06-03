package wxdgaming.game.server.script.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.message.task.ResTaskList;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnTask;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.task.init.ConditionInitValueHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 任务模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:43
 **/
@Singleton
public class TaskService extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private HashMap<TaskType, ITaskScript> taskScriptImplHashMap = new HashMap<>();
    private HashMap<Condition, ConditionInitValueHandler> conditionInitValueHandlerMap = new HashMap<>();

    @Inject
    public TaskService(DataCenterService dataCenterService) {
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

        HashMap<TaskType, ITaskScript> tmpTaskScriptImplHashMap = new HashMap<>();
        runApplication.classWithSuper(ITaskScript.class)
                .forEach(taskScript -> {
                    if (tmpTaskScriptImplHashMap.put(taskScript.type(), taskScript) != null) {
                        throw new RuntimeException("任务类型处理重复：" + taskScript.type());
                    }
                });
        taskScriptImplHashMap = tmpTaskScriptImplHashMap;
    }

    public ITaskScript getTaskScript(TaskType taskType) {
        ITaskScript taskScript = taskScriptImplHashMap.get(taskType);
        AssertUtil.assertNull(taskScript, "任务类型不存在：" + taskType);
        return taskScript;
    }

    /** 初始化任务的时候调用变量初始化，比如接取任务的初始化当前等级 */
    public void initTask(Player player, TaskInfo taskInfo, List<Condition> conditionList) {
        for (int i = 0; i < conditionList.size(); i++) {
            Condition condition = conditionList.get(i);
            ConditionInitValueHandler conditionInitValueHandler = conditionInitValueHandlerMap.get(condition);
            if (conditionInitValueHandler != null) {
                long initValue = conditionInitValueHandler.initValue(player, condition);
                /*设置初始化变量*/
                taskInfo.getProgresses().put(i, initValue);
            }
        }
    }

    /** 登录的时候检查任务 */
    @OnLogin
    public void onLogin(Player player) {
        TaskPack taskPack = player.getTaskPack();
        /*推送数据的*/
        ResTaskList resTaskList = new ResTaskList();
        taskPack.getTasks().forEach(taskInfo -> {
            TaskBean taskBean = taskInfo.buildTaskBean();
            resTaskList.getTasks().add(taskBean);
        });
        player.write(resTaskList);
    }

    @OnTask
    public void update(Player player, Condition condition) {
        TaskPack taskPack = player.getTaskPack();
        List<TaskInfo> changes = new ArrayList<>();
        for (ITaskScript taskScript : taskScriptImplHashMap.values()) {
            taskScript.update(player, taskPack, changes, condition);
        }
        if (changes.isEmpty()) {
            return;
        }
        ResUpdateTaskList resUpdateTaskList = new ResUpdateTaskList();
        /* TODO发送变更列表 */
        for (TaskInfo taskInfo : changes) {
            TaskBean taskBean = taskInfo.buildTaskBean();
            resUpdateTaskList.getTasks().add(taskBean);
        }
        player.write(resUpdateTaskList);
    }

}
