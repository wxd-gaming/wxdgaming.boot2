package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.event.EventBus;
import wxdgaming.game.test.script.role.PlayerScript;
import wxdgaming.game.test.script.role.message.ReqLogin;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler {

    private final EventBus eventBus;
    private final DataCenterService dataCenterService;
    private final PlayerScript playerScript;


    @Inject
    public ReqLoginHandler(EventBus eventBus, DataCenterService dataCenterService, PlayerScript playerScript) {
        this.eventBus = eventBus;
        this.dataCenterService = dataCenterService;
        this.playerScript = playerScript;
    }

    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {

        log.info("登录请求:{}", req);

        int sid = req.getSid();
        String account = req.getAccount();
        String token = req.getToken();

        Jws<Claims> claimsJws = JwtUtils.parseJWT(token);
        String tokenAccount = claimsJws.getPayload().get("account", String.class);
        String platform = claimsJws.getPayload().get("platform", String.class);
        String channel = claimsJws.getPayload().get("channel", String.class);

        socketSession.attribute("account", account);
        socketSession.attribute("sid", sid);

        playerScript.sendPlayerList(socketSession, sid, account);

        log.info("登录完成:{}", account);
    }

}