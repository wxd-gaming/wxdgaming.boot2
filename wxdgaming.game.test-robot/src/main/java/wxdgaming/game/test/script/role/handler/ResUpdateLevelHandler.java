package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.Robot;
import wxdgaming.game.test.script.role.message.ResUpdateLevel;

/**
 * 更新等级
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResUpdateLevelHandler {

    /** 更新等级 */
    @ProtoRequest
    public void resUpdateLevel(SocketSession socketSession, ResUpdateLevel req) {
        Robot robot = socketSession.attribute("robot");
        robot.setLevel(req.getLevel());
        log.info("{} 更新等级:{}", robot, req);
    }

}