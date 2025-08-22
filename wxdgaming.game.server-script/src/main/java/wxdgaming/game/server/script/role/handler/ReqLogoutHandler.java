package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.message.role.ReqLogout;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;

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
    @ProtoRequest(ReqLogout.class)
    public void reqLogout(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqLogout message = event.buildMessage();
        UserMapping userMapping = event.bindData();
        Player player = userMapping.player();

    }

}