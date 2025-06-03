package wxdgaming.game.robot.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;

import java.util.List;

/**
 * 更新任务列表
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateTaskListHandler {

    /** 更新任务列表 */
    @ProtoRequest
    public void resUpdateTaskList(SocketSession socketSession, ResUpdateTaskList req) {
        Robot robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 任务变更列表 {}", robot, req);
        }
        List<TaskBean> tasks = req.getTasks();
        for (TaskBean task : tasks) {
            robot.getTasks().put(task.getTaskId(), task);
        }
    }

}