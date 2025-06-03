package wxdgaming.game.server.script.task;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.core.Reason;
import wxdgaming.game.message.task.ResAcceptTask;
import wxdgaming.game.message.task.ResSubmitTask;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.cfg.QTaskTable;
import wxdgaming.game.server.cfg.bean.QTask;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.script.goods.BagService;
import wxdgaming.game.server.script.inner.InnerService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.*;

/**
 * 任务基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-03 15:15
 */
@Slf4j
public abstract class ITaskScript extends HoldRunApplication {

    @Inject protected BagService bagService;
    @Inject protected TaskService taskService;
    @Inject protected TipsService tipsService;
    @Inject protected InnerService innerService;

    public abstract TaskType type();

    /** 登录的时候检查任务 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        TaskPack taskPack = player.getTaskPack();
        initTask(player, taskPack);
    }

    protected TaskInfo buildTaskInfo(Player player, QTask qTask) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setCfgId(qTask.getId());
        taskService.initTask(player, taskInfo, qTask.getConditionList()/*TODO读取配置表*/);
        return taskInfo;
    }

    /** 初始化 */
    public void initTask(Player player, TaskPack taskPack) {
        Map<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().row(type());
        if (integerTaskInfoMap.isEmpty()) {
            QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
            TreeMap<Integer, QTask> integerQTaskTreeMap = qTaskTable.getTaskGroupMap().get(type());
            QTask qTask = integerQTaskTreeMap.firstEntry().getValue();
            acceptTask(player, taskPack, qTask.getId());
        }
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

    /** 接受任务 */
    public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        TreeMap<Integer, QTask> integerQTaskTreeMap = qTaskTable.getTaskGroupMap().get(type());
        QTask qTask = integerQTaskTreeMap.get(taskId);
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);
        if (taskInfo == null) {
            taskInfo = buildTaskInfo(player, qTask);
            taskPack.getTasks().put(type(), taskInfo.getCfgId(), taskInfo);
        }
        taskInfo.setAcceptTime(System.currentTimeMillis());
        log.info("{} 接取 {} 任务：{}({}), {}", player, type(), qTask.getId(), qTask.getName(), taskInfo.toJSONString());
        ResAcceptTask resAcceptTask = new ResAcceptTask();
        resAcceptTask.setTaskType(type());
        resAcceptTask.setTaskId(taskId);
        resAcceptTask.setTask(taskInfo.buildTaskBean());
        player.write(resAcceptTask);
    }

    /** 提交任务 */
    public void submitTask(Player player, TaskPack taskPack, int taskId) {
        Map<Integer, TaskInfo> taskInfoMap = taskPack.getTasks().row(type());
        TaskInfo taskInfo = taskInfoMap.get(taskId);

        if (!taskInfo.isComplete()) {
            log.error("{} 任务 {} 没有完成", player, taskId);
            tipsService.tips(player, "任务尚未完成");
            return;
        }

        QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        TreeMap<Integer, QTask> integerQTaskTreeMap = qTaskTable.getTaskGroupMap().get(type());
        QTask qTask = integerQTaskTreeMap.get(taskInfo.getCfgId());
        List<ItemCfg> rewards = qTask.getRewards();

        taskInfo.setRewards(true);

        long serialNumber = System.nanoTime();

        log.info("{} 完成任务 {}({}), 流水号：{}, rewards={}", player, qTask.getId(), qTask.getName(), serialNumber, rewards);

        bagService.gainItems4Cfg(player, serialNumber, Reason.TASK_SUBMIT, rewards, "taskCfg=", taskId);

        taskPack.getTaskFinishList().computeIfAbsent(type(), k -> new ArrayList<>()).add(taskId);
        ResSubmitTask resSubmitTask = new ResSubmitTask();
        resSubmitTask.setTaskType(type());
        resSubmitTask.setTaskId(taskId);
        QTask nextQTask = integerQTaskTreeMap.get(qTask.getAfter());
        if (nextQTask != null) {
            resSubmitTask.setRemove(true);
            taskInfoMap.remove(taskId);
        }
        player.write(resSubmitTask);

        if (nextQTask != null) {
            acceptTask(player, taskPack, nextQTask.getId());
        }
    }

}
