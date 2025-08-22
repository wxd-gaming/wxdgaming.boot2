package wxdgaming.game.server.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.task.ReqSubmitTask;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.script.task.ITaskScript;
import wxdgaming.game.server.script.task.TaskService;

/**
 * 提交任务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqSubmitTaskHandler {

    TaskService taskService;

    public ReqSubmitTaskHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /** 提交任务 */
    @ProtoRequest(ReqSubmitTask.class)
    public void reqSubmitTask(ProtoEvent event) {
        ReqSubmitTask req = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();
        TaskType taskType = req.getTaskType();
        int taskId = req.getTaskId();
        ITaskScript taskScript = taskService.getTaskScript(taskType);
        TaskPack taskPack = player.getTaskPack();
        taskScript.submitTask(player, taskPack, taskId);
    }

}