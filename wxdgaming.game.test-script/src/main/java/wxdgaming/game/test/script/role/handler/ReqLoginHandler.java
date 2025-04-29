package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.role.PlayerScript;
import wxdgaming.game.test.script.role.message.ReqLogin;
import wxdgaming.game.test.script.tips.TipsScript;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private final PlayerScript playerScript;
    private final TipsScript tipsScript;

    @Inject
    public ReqLoginHandler(DataCenterService dataCenterService, PlayerScript playerScript, TipsScript tipsScript) {
        this.dataCenterService = dataCenterService;
        this.playerScript = playerScript;
        this.tipsScript = tipsScript;
    }

    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {

        log.info("登录请求:{}", req);
        try {
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
        } catch (Exception e) {
            log.error("登录失败 {}", req, e);
            tipsScript.tips(socketSession, "服务器异常");
        }
    }

}