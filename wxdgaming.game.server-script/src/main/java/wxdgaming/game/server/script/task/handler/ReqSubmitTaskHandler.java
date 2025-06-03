package wxdgaming.game.server.script.task.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ReqSubmitTask;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.script.task.ITaskScript;
import wxdgaming.game.server.script.task.TaskService;

/**
 * 提交任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqSubmitTaskHandler {

    TaskService taskService;

    @Inject
    public ReqSubmitTaskHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /** 提交任务 */
    @ProtoRequest
    public void reqSubmitTask(SocketSession socketSession, ReqSubmitTask req,
                              @ThreadParam(path = "player") Player player) {
        TaskType taskType = req.getTaskType();
        int taskId = req.getTaskId();
        ITaskScript taskScript = taskService.getTaskScript(taskType);
        TaskPack taskPack = player.getTaskPack();
        taskScript.submitTask(player, taskPack, taskId);
    }

}