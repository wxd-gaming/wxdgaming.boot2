package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.inner.ReqRegisterServer;

import java.util.List;

/**
 * null
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqRegisterServerHandler {

    /** null */
    @ProtoRequest
    public void reqRegisterServer(SocketSession socketSession, ReqRegisterServer req) {

        int gameId = req.getGameId();
        List<Integer> serverIds = req.getServerIds();
        List<Integer> messageIds = req.getMessageIds();

    }

}