package wxdgaming.game.server.script.task.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.cfg.QTaskTable;
import wxdgaming.game.server.cfg.bean.QTask;
import wxdgaming.game.server.script.task.ITaskScript;

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
        Map<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().row(type());
        if (integerTaskInfoMap.isEmpty()) {
            QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
            QTask qTask = qTaskTable.get(1);
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setCfgId(qTask.getId());
            taskInfo.setAcceptTime(System.currentTimeMillis());
            taskService.initTask(player, taskInfo, Map.of()/*TODO读取配置表*/);
            taskPack.getTasks().put(type(), taskInfo.getCfgId(), taskInfo);
            log.info("{} 初始化任务：{}", player, taskInfo);
        }
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
