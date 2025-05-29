package wxdgaming.game.gateway.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.inner.ReqForwardMessage;
import wxdgaming.game.message.role.ResHeartbeat;

/**
 * 心跳包响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResHeartbeatHandler {

    /** 心跳包响应 */
    public void resHeartbeat(SocketSession socketSession, ResHeartbeat req, @ThreadParam(path = "forwardMessage") ReqForwardMessage forwardMessage) {

    }

}