package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.role.message.ReqHeartbeat;
import wxdgaming.game.test.script.role.message.ResHeartbeat;

/**
 * null
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqHeartbeatHandler {

    /** null */
    @ProtoRequest
    public void reqHeartbeat(SocketSession socketSession, ReqHeartbeat req) {
        ResHeartbeat resHeartbeat = new ResHeartbeat();
        resHeartbeat.setTimestamp(MyClock.millis());
        socketSession.write(resHeartbeat);
    }

}