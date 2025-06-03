package wxdgaming.game.server.script.task;

import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.message.task.ResTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.script.goods.BagService;

import java.util.*;

public abstract class ITaskScript extends HoldRunApplication {

    protected BagService bagService;
    protected TaskService taskService;

    @Init
    public void init(TaskService taskService, BagService bagService) {
        this.taskService = taskService;
        this.bagService = bagService;
    }

    public abstract TaskType type();

    /** 登录的时候检查任务 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        TaskPack taskPack = player.getTaskPack();
        initTask(player, taskPack);
    }

    /** 登录的时候检查任务 */
    @OnLogin
    public void onLogin(Player player) {
        TaskPack taskPack = player.getTaskPack();
        /*推送数据的*/
        ResTaskList resTaskList = new ResTaskList();
        resTaskList.setTaskType(type());
        Map<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().get(type());
        if (integerTaskInfoMap != null) {
            for (TaskInfo value : integerTaskInfoMap.values()) {
                TaskBean taskBean = new TaskBean();
                taskBean.setTaskId(value.getCfgId());
                HashMap<Integer, Long> progresses = value.getProgresses();
                progresses.entrySet()
                        .stream()
                        .sorted(Comparator.comparingInt(Map.Entry::getKey))
                        .map(Map.Entry::getValue)
                        .forEach(taskBean.getProgresses()::add);
                resTaskList.getTasks().add(taskBean);
            }
        }
        player.write(resTaskList);
    }

    /** 初始化 */
    public abstract void initTask(Player player, TaskPack taskPack);

    /** 接受任务 */
    public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);
        taskInfo.setAcceptTime(System.currentTimeMillis());
    }

    /** 更新 */
    public void update(Player player, TaskPack taskPack, List<TaskInfo> changes, Condition condition) {
        Collection<TaskInfo> taskInfos = taskPack.getTasks().row(type()).values();
        for (TaskInfo taskInfo : taskInfos) {
            if (taskInfo.isComplete()) {
                continue;
            }
            boolean update = taskInfo.update(condition);
            if (update) {
                changes.add(taskInfo);
            }
        }
    }

    /** 提交任务 */
    public void submitTask(Player player, TaskPack taskPack, int taskId) {
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);

        ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        List<ItemCfg> rewards = new ArrayList<>();
        rewards.add(builder.cfgId(10001).num(100).build());
        rewards.add(builder.cfgId(30001).num(100).build());
        bagService.gainItems4Cfg(player, System.nanoTime(), rewards, "完成任务:", taskId);

    }

}
