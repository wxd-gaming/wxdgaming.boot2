package wxdgaming.game.server.script.task.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.cfg.bean.QTask;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.script.task.ITaskScript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主线任务实现类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 10:46
 **/
@Slf4j
@Singleton
public class TaskMainScript extends ITaskScript {

    @Override public TaskType type() {
        return TaskType.Main;
    }

    /** 初始化 */
    @Override public void initTask(Player player, TaskPack taskPack) {
        super.initTask(player, taskPack);

        HashMap<Integer, TaskInfo> integerTaskInfoHashMap = taskPack.getTasks().get(type());
        if (integerTaskInfoHashMap.isEmpty()) {return;}
        Map.Entry<Integer, TaskInfo> next = integerTaskInfoHashMap.entrySet().iterator().next();
        TaskInfo taskInfo = next.getValue();
        if (taskInfo == null) {return;}
        if (taskInfo.isRewards()) {
            /*触发下一个任务*/
            QTask qTask = taskInfo.qTask();
            QTask qTaskAfter = qTask.getQTaskAfter();
            if (qTaskAfter != null) {
                integerTaskInfoHashMap.remove(taskInfo.getCfgId());
                initTaskInfo(player, taskPack, qTaskAfter, false);
            }
        }
    }

    @Override protected TaskInfo initTaskInfo(Player player, TaskPack taskPack, QTask qTask, boolean noticeClient) {
        return super.initTaskInfo(player, taskPack, qTask, noticeClient);
    }

    /** 接受任务 */
    @Override public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        super.acceptTask(player, taskPack, taskId);
    }

    /** 更新 */
    @Override public void update(Player player, TaskPack taskPack, List<TaskInfo> changes, Condition condition) {
        super.update(player, taskPack, changes, condition);
    }

    /** 提交任务 */
    @Override public void submitTask(Player player, TaskPack taskPack, int taskId) {
        super.submitTask(player, taskPack, taskId);
    }

}
