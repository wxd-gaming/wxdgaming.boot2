package wxdgaming.game.server.script.task.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.cfg.bean.QTask;
import wxdgaming.game.server.script.task.ITaskScript;

import java.util.List;

/**
 * 主线任务实现类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 10:46
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
    }

    @Override protected TaskInfo buildTaskInfo(Player player, QTask qTask) {
        TaskInfo taskInfo = super.buildTaskInfo(player, qTask);
        taskInfo.setAcceptTime(System.currentTimeMillis());
        return taskInfo;
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
