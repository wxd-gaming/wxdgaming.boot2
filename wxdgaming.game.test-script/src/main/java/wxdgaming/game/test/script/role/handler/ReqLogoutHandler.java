package wxdgaming.game.test.script.role.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.script.role.message.ReqLogout;

/**
 * null
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLogoutHandler {

    /** null */
    @ProtoRequest
    public void reqLogout(SocketSession socketSession, ReqLogout req) {

    }

}