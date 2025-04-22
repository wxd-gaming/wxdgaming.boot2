package wxdgaming.game.test.script.task.impl;

import com.google.inject.Singleton;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskInfo;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.task.ITaskScript;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 主线任务实现类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 10:46
 **/
@Singleton
public class TaskMainScript extends ITaskScript {

    @Override public int type() {
        return 1;
    }

    /** 初始化 */
    @Override public void initTask(Player player, TaskPack taskPack) {
        ArrayList<TaskInfo> taskInfos = taskPack.getTasks().computeIfAbsent(type(), l -> new ArrayList<>());
        if (taskInfos.isEmpty()) {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setCfgId(1);
            /*TODO 考虑初始化进度，比如当前等级*/
            taskInfos.add(taskInfo);
        }
    }

    /** 接受任务 */
    @Override public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        ArrayList<TaskInfo> taskInfos = taskPack.getTasks().get(type());
        TaskInfo taskInfo = taskInfos.getFirst();
    }

    /** 更新 */
    @Override public void update(Player player, TaskPack taskPack, Serializable k1, Serializable k2, Serializable k3, long targetValue) {
        ArrayList<TaskInfo> taskInfos = taskPack.getTasks().get(type());
        TaskInfo taskInfo = taskInfos.getFirst();
    }

    /** 提交任务 */
    @Override public void submitTask(Player player, TaskPack taskPack, int taskId) {
        ArrayList<TaskInfo> taskInfos = taskPack.getTasks().get(type());
        TaskInfo taskInfo = taskInfos.getFirst();
    }

}
