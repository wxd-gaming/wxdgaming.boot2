package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ResUpdateLevel;
import wxdgaming.game.robot.bean.Robot;

/**
 * 更新等级
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateLevelHandler {

    /** 更新等级 */
    @ProtoRequest
    public void resUpdateLevel(SocketSession socketSession, ResUpdateLevel req) {
        Robot robot = socketSession.bindData("robot");
        robot.setLevel(req.getLevel());
        log.info("{} 更新等级:{}", robot, req);
    }

}