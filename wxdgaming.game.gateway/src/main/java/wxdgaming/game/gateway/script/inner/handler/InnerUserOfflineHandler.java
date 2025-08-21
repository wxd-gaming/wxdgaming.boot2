package wxdgaming.game.gateway.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.inner.InnerUserOffline;

/**
 * 玩家离线
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerUserOfflineHandler {

    /** 玩家离线 */
    @ProtoRequest
    public void innerUserOffline(SocketSession socketSession, InnerUserOffline req) {
        
    }

}