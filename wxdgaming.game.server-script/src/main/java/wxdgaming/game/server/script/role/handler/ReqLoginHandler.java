package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.basic.login.LoginProperties;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.role.PlayerService;
import wxdgaming.game.server.script.tips.TipsService;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqLoginHandler extends HoldApplicationContext {

    private final DataCenterService dataCenterService;
    private final PlayerService playerService;
    private final TipsService tipsService;
    private final LoginProperties loginProperties;

    public ReqLoginHandler(DataCenterService dataCenterService,
                           PlayerService playerService,
                           TipsService tipsService,
                           LoginProperties loginProperties) {
        this.dataCenterService = dataCenterService;
        this.playerService = playerService;
        this.tipsService = tipsService;
        this.loginProperties = loginProperties;
    }

    @ProtoRequest(ReqLogin.class)
    public void reqLogin(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqLogin req = event.buildMessage();
        String clientIp = socketSession.getIP();
        log.info("登录请求: clientSession={}, {}", socketSession, req);
        try {
            int sid = req.getSid();
            String token = req.getToken();
            JsonToken jsonToken = JsonTokenParse.parse(loginProperties.getJwtKey(), token);
            int appId = jsonToken.getIntValue("appId");
            String account = jsonToken.getString("account");
            String platform = jsonToken.getString("platform");
            /*平台返回的userid*/
            String platformUserId = jsonToken.getString("platformUserId");


            UserMapping userMapping = dataCenterService.getUserMapping(account);

            userMapping.setSid(sid);
            userMapping.setAppId(appId);
            userMapping.setClientIp(clientIp);
            userMapping.setPlatform(platform);
            userMapping.setPlatformUserId(platformUserId);
            userMapping.setSocketSession(socketSession);
            userMapping.setClientParams(req.getClientParams());

            socketSession.bindData("userMapping", userMapping);

            playerService.sendPlayerList(socketSession, sid, account);

            log.info("登录完成:{}", userMapping);
        } catch (Exception e) {
            log.error("登录失败 {}", req, e);
            tipsService.tips(socketSession, "服务器异常");
        }
    }

}