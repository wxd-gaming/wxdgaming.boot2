package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.role.message.ReqHeartbeat;

/**
 * 
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqHeartbeatHandler {

    @ProtoRequest
    public void reqHeartbeat(SocketSession socketSession, ReqHeartbeat req) {

    }

}