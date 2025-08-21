package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ResHeartbeat;

/**
 * null
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResHeartbeatHandler {

    /** null */
    @ProtoRequest(ResHeartbeat.class)
    public void resHeartbeat(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ResHeartbeat req = event.buildMessage();
        Object robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 心跳成功 {}", robot, req.getTimestamp());
        }
    }

}