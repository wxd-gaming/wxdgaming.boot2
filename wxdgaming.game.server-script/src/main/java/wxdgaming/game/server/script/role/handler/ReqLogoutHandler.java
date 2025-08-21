package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqLogout;
import wxdgaming.game.server.bean.ClientSessionMapping;

/**
 * null
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqLogoutHandler {

    /** null */
    @ProtoRequest
    public void reqLogout(SocketSession socketSession, ReqLogout req, @ThreadParam(path = "clientSessionMapping") ClientSessionMapping clientSessionMapping) {

    }

}