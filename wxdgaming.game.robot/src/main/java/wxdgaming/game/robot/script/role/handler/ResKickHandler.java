package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ResKick;
import wxdgaming.game.robot.bean.Robot;

/**
 * 踢下线
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResKickHandler {

    /** 踢下线 */
    @ProtoRequest(ResKick.class)
    public void resKick(ProtoEvent event) {
        ResKick message = event.buildMessage();
        Robot robot = event.getSocketSession().bindData("robot");
        log.info("[ResKick] {} {}", robot, message);
        robot.setLoginEnd(false);
        robot.setSendLogin(false);
    }

}