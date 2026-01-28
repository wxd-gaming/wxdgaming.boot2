package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.executor.ExecutorContext;
import wxdgaming.boot2.core.executor.ExecutorLog;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.token.JsonToken;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.game.common.bean.ban.BanType;
import wxdgaming.game.common.bean.login.ConnectLoginProperties;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.login.bean.UserDataVo;
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
    final GlobalDataService globalDataService;
    private final PlayerService playerService;
    private final TipsService tipsService;
    private final ConnectLoginProperties connectLoginProperties;

    public ReqLoginHandler(DataCenterService dataCenterService, GlobalDataService globalDataService,
                           PlayerService playerService,
                           TipsService tipsService,
                           ConnectLoginProperties connectLoginProperties) {
        this.dataCenterService = dataCenterService;
        this.globalDataService = globalDataService;
        this.playerService = playerService;
        this.tipsService = tipsService;
        this.connectLoginProperties = connectLoginProperties;
    }

    @ProtoRequest(ReqLogin.class)
    @ExecutorWith(queueName = "login")
    @ExecutorLog(executorWarnTime = -1)
    public void reqLogin(ProtoEvent event) {
        SocketSession socketSession = event.getSocketSession();
        ReqLogin req = event.buildMessage();
        String clientIp = socketSession.getIP();
        log.info("登录请求: clientSession={}, {}", socketSession, req);
        try {
            int sid = req.getSid();
            String token = req.getToken();
            ExecutorContext.context().startWatch("token解码");
            JsonToken jsonToken = JsonTokenParse.parse(connectLoginProperties.getJwtKey(), token);
            UserDataVo userDataVo = jsonToken.getObject("user", UserDataVo.class);
            ExecutorContext.context().stopWatch();

            if (globalDataService.checkBan(BanType.AccountLogin, userDataVo.getAccount())) {
                tipsService.tips(socketSession, "账号被封禁");
                return;
            }

            UserMapping userMapping = dataCenterService.getUserMapping(userDataVo.getAccount());

            userMapping.setSid(sid);
            userMapping.setClientIp(clientIp);
            userMapping.setUserDataVo(userDataVo);
            userMapping.setSocketSession(socketSession);
            userMapping.setClientParams(req.getClientParams());

            socketSession.bindData("userMapping", userMapping);

            playerService.sendPlayerList(socketSession, sid, userDataVo.getAccount());

            log.info("登录完成:{}", userMapping);
        } catch (Exception e) {
            log.error("登录失败 {}", req, e);
            tipsService.tips(socketSession, "服务器异常");
        }
    }

}