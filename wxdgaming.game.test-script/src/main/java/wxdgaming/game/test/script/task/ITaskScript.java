package wxdgaming.game.test.script.task;

import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.event.OnLogin;
import wxdgaming.game.test.script.event.OnLoginBefore;
import wxdgaming.game.test.script.goods.BagService;

import java.io.Serializable;
import java.util.*;

public abstract class ITaskScript extends HoldRunApplication {

    protected BagService bagService;
    protected TaskService taskService;

    @Init
    public void init(TaskService taskService, BagService bagService) {
        this.taskService = taskService;
        this.bagService = bagService;
    }

    public abstract int type();

    public TaskPack getTaskPack(Player player) {
        return taskService.getTaskPack(player);
    }

    /** 登录的时候检查任务 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        TaskPack taskPack = getTaskPack(player);
        initTask(player, taskPack);
    }

    /** 登录的时候检查任务 */
    @OnLogin
    public void onLogin(Player player) {
        TaskPack taskPack = getTaskPack(player);
        /*推送数据的*/
    }

    /** 初始化 */
    public abstract void initTask(Player player, TaskPack taskPack);

    /** 接受任务 */
    public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);
    }

    /** 更新 */
    public void update(Player player, TaskPack taskPack, List<TaskInfo> changes, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        Collection<TaskInfo> taskInfos = taskPack.getTasks().get(type()).values();
        for (TaskInfo taskInfo : taskInfos) {
            HashMap<Integer, Long> progresses = taskInfo.getProgresses();
            int completeSize = 0;
            Map<Integer, Long> taskTargets = new HashMap<>();
            taskTargets.put(1, 1L);
            /*TODO 这里应该读取配置表循环条件*/
            for (Map.Entry<Integer, Long> entry : taskTargets.entrySet()) {
                Integer conditionId = entry.getKey();/*条件id*/
                Long targetProgress = entry.getValue();/*条件完成目标*/
                Condition condition = taskService.getCondition(conditionId);
                if (condition.equals(k1, k2, k3)) {
                    Long progress = progresses.getOrDefault(conditionId, 0L);
                    long update = condition.getUpdateType().update(progress, targetValue);
                    taskInfo.getProgresses().put(conditionId, update);
                    if (update >= targetProgress) {
                        completeSize++;
                    }
                }
            }
            if (completeSize > 0) {
                completeSize = 0;
                for (Map.Entry<Integer, Long> entry : taskTargets.entrySet()) {
                    Integer conditionId = entry.getKey();/*条件id*/
                    Long targetProgress = entry.getValue();/*条件完成目标*/
                    Long progress = progresses.getOrDefault(conditionId, 0L);
                    if (progress >= targetProgress) {
                        completeSize++;
                    }
                }
                if (completeSize >= taskTargets.size()) {
                    taskInfo.setComplete(true);
                }
            }
        }
    }

    /** 提交任务 */
    public void submitTask(Player player, TaskPack taskPack, int taskId) {
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);

        ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        List<ItemCfg> rewards = new ArrayList<>();
        rewards.add(builder.cfgId(10001).count(100).build());
        rewards.add(builder.cfgId(30001).count(100).build());
        bagService.gainItems4Cfg(player, System.nanoTime(), rewards, "完成任务:", taskId);

    }

}
