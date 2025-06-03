package wxdgaming.game.robot.script.task.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResTaskList;
import wxdgaming.game.robot.bean.Robot;

/**
 * 任务列表
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResTaskListHandler {

    /** 任务列表 */
    @ProtoRequest
    public void resTaskList(SocketSession socketSession, ResTaskList req) {
        Robot robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 任务列表 {}", robot, req);
        }
    }

}