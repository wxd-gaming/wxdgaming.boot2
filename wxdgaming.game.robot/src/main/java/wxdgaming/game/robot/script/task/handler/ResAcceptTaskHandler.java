package wxdgaming.game.robot.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResAcceptTask;
import wxdgaming.game.robot.bean.Robot;

/**
 * 接受任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResAcceptTaskHandler {

    /** 接受任务 */
    @ProtoRequest
    public void resAcceptTask(SocketSession socketSession, ResAcceptTask req) {
        Robot robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 接取任务 {}", robot, req);
        }
        robot.getTasks().put(req.getTaskId(), req.getTask());
    }

}