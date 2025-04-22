package wxdgaming.game.test.script.task.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;
import wxdgaming.game.test.script.task.ITaskScript;
import wxdgaming.game.test.script.task.TaskModuleScript;
import wxdgaming.game.test.script.task.message.ReqAcceptTask;

/**
 * 接受任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqAcceptTaskHandler {

    TaskModuleScript taskModuleScript;

    @Inject
    public ReqAcceptTaskHandler(TaskModuleScript taskModuleScript) {
        this.taskModuleScript = taskModuleScript;
    }

    /** 接受任务 */
    @ProtoRequest
    public void reqAcceptTask(SocketSession socketSession, ReqAcceptTask req) {
        Player player = socketSession.attribute("player");
        int taskType = req.getTaskType().getCode();
        int taskId = req.getTaskId();
        TaskPack taskPack = taskModuleScript.getTaskPack(player);
        ITaskScript taskScript = taskModuleScript.getTaskScript(taskType);
        taskScript.acceptTask(player, taskPack, taskId);
    }

}