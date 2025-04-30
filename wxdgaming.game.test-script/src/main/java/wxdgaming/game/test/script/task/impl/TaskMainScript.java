package wxdgaming.game.test.script.task.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.task.ITaskScript;
import wxdgaming.game.test.script.task.TaskEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 主线任务实现类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 10:46
 **/
@Slf4j
@Singleton
public class TaskMainScript extends ITaskScript {

    @Override public int type() {
        return 1;
    }

    /** 初始化 */
    @Override public void initTask(Player player, TaskPack taskPack) {
        Map<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().get(type());
        if (integerTaskInfoMap == null || integerTaskInfoMap.isEmpty()) {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setCfgId(1);
            taskService.initTask(player, taskInfo, null/*TODO读取配置表*/);
            taskPack.getTasks().put(type(), taskInfo.getCfgId(), taskInfo);
            log.info("{} 初始化任务：{}", player, taskInfo);
        }
    }

    /** 接受任务 */
    @Override public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        super.acceptTask(player, taskPack, taskId);
    }

    /** 更新 */
    @Override public void update(Player player, TaskPack taskPack, List<TaskInfo> changes, TaskEvent taskEvent) {
        super.update(player, taskPack, changes, taskEvent);
    }

    /** 提交任务 */
    @Override public void submitTask(Player player, TaskPack taskPack, int taskId) {
        super.submitTask(player, taskPack, taskId);
    }

}
