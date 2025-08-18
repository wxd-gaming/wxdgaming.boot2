package wxdgaming.game.server.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.message.global.MapBean;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.ArrayList;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ReqLoginHandler extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private final ClientSessionService clientSessionService;
    private final PlayerService playerService;
    private final TipsService tipsService;
    private final LoginProperties loginProperties;

    @Inject
    public ReqLoginHandler(DataCenterService dataCenterService,
                           ClientSessionService clientSessionService,
                           PlayerService playerService,
                           TipsService tipsService,
                           LoginProperties loginProperties) {
        this.dataCenterService = dataCenterService;
        this.clientSessionService = clientSessionService;
        this.playerService = playerService;
        this.tipsService = tipsService;
        this.loginProperties = loginProperties;
    }

    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {
        long clientSessionId = ThreadContext.context().getLongValue("clientSessionId");
        String clientIp = ThreadContext.context().getString("clientIp");
        log.info("登录请求:{}, clientSessionId={}", req, clientSessionId);
        try {
            int sid = req.getSid();
            String token = req.getToken();
            JsonToken jsonToken = JsonTokenParse.parse(loginProperties.getJwtKey(), token);
            int appId = jsonToken.getIntValue("appId");
            String account = jsonToken.getString("account");
            String platform = jsonToken.getString("platform");
            /*平台返回的userid*/
            String platformUserId = jsonToken.getString("platformUserId");

            int gatewayId = socketSession.bindData("serviceId");

            ClientSessionMapping clientSessionMapping = clientSessionService.getMapping(account);

            clientSessionMapping.setSid(sid);
            clientSessionMapping.setAccount(account);
            clientSessionMapping.setAppId(appId);
            clientSessionMapping.setClientIp(clientIp);
            clientSessionMapping.setPlatform(platform);
            clientSessionMapping.setPlatformUserId(platformUserId);
            clientSessionMapping.setSession(socketSession);
            clientSessionMapping.setGatewayId(gatewayId);
            clientSessionMapping.setClientParams((ArrayList<MapBean>) req.getClientParams());
            clientSessionMapping.setClientSessionId(clientSessionId);

            playerService.sendPlayerList(socketSession, clientSessionId, sid, account);

            log.info("登录完成:{}", clientSessionMapping);
        } catch (Exception e) {
            log.error("登录失败 {}", req, e);
            tipsService.tips(socketSession, clientSessionId, "服务器异常");
        }
    }

}