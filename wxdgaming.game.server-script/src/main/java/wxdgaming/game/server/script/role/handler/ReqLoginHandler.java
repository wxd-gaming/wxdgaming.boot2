package wxdgaming.game.server.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.tips.TipsService;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private final PlayerService playerService;
    private final TipsService tipsService;

    @Inject
    public ReqLoginHandler(DataCenterService dataCenterService, PlayerService playerService, TipsService tipsService) {
        this.dataCenterService = dataCenterService;
        this.playerService = playerService;
        this.tipsService = tipsService;
    }

    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {
        long clientSessionId = ThreadContext.context().getLongValue("clientSessionId");
        log.info("登录请求:{}, clientSessionId={}", req, clientSessionId);
        try {
            int sid = req.getSid();
            String account = req.getAccount();
            String token = req.getToken();
            Jws<Claims> claimsJws = JwtUtils.parseJWT(token);
            String tokenAccount = claimsJws.getPayload().get("account", String.class);
            String platform = claimsJws.getPayload().get("platform", String.class);
            String channel = claimsJws.getPayload().get("channel", String.class);

            socketSession
                    .bindData("sid", sid)
                    .bindData("account", account)
                    .bindData("platform", platform);

            playerService.sendPlayerList(socketSession, sid, account);

            log.info("登录完成:{}", account);
        } catch (Exception e) {
            log.error("登录失败 {}", req, e);
            tipsService.tips(socketSession, "服务器异常");
        }
    }

}