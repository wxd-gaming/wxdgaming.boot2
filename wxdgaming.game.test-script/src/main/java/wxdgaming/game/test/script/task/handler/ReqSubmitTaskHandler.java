package wxdgaming.game.test.script.task.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.task.ITaskScript;
import wxdgaming.game.test.script.task.TaskService;
import wxdgaming.game.test.script.task.message.ReqSubmitTask;

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
    public void reqSubmitTask(SocketSession socketSession, ReqSubmitTask req) {
        Player player = socketSession.bindData("player");
        int taskType = req.getTaskType().getCode();
        int taskId = req.getTaskId();
        ITaskScript taskScript = taskService.getTaskScript(taskType);
        TaskPack taskPack = player.getTaskPack();
        taskScript.submitTask(player, taskPack, taskId);
    }

}