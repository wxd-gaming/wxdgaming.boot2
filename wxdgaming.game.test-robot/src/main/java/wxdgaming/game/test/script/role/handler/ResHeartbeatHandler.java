package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.role.message.ResHeartbeat;

/**
 * null
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResHeartbeatHandler {

    /** null */
    @ProtoRequest
    public void resHeartbeat(SocketSession socketSession, ResHeartbeat req) {
        Object robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 心跳成功 {}", robot, req.getTimestamp());
        }
    }

}