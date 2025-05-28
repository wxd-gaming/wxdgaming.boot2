package wxdgaming.game.robot.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ResUpdateExp;
import wxdgaming.game.robot.bean.Robot;

/**
 * 更新经验
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateExpHandler {

    /** 更新经验 */
    @ProtoRequest
    public void resUpdateExp(SocketSession socketSession, ResUpdateExp req) {
        Robot robot = socketSession.bindData("robot");
        robot.setExp(req.getExp());
        log.info("{} 更新经验:{}", robot, req);
    }

}