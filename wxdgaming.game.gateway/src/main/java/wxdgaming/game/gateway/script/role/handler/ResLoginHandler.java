package wxdgaming.game.gateway.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.bean.UserMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.message.inner.InnerUserOffline;
import wxdgaming.game.message.role.ResLogin;

import java.util.List;
import java.util.Objects;

/**
 * 登录响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Singleton
public class ResLoginHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ResLoginHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }


    /** 登录响应 */
    @ProtoRequest
    public void resLogin(SocketSession socketSession, ResLogin req) {
        InnerForwardMessage forwardMessage = ThreadContext.context("forwardMessage");
        List<Long> sessionIds = forwardMessage.getSessionIds();
        final Long clientSessionId = sessionIds.getFirst();
        final String account = req.getAccount();
        SocketSession clientSession = dataCenterService.getClientSession(clientSessionId);
        clientSession.bindData("account", account);

        UserMapping userMapping = dataCenterService.getUserMapping(account);
        userMapping.setGameServerId(req.getSid());

        clientSession.bindData("userMapping", userMapping);

        if (userMapping.getClientSocketSession() != null && !Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
            log.info("玩家被顶号登录：{}, {}, {}", account, clientSessionId, userMapping);
            userMapping.getClientSocketSession().close("被顶号登录");
        }
        if (userMapping.getClientSocketSession() == null || !Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
            clientSession.getChannel().closeFuture().addListener(future -> {
                if (!Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
                    return;
                }

                ServerMapping gameServerMapping = dataCenterService.getGameServerMapping(userMapping.getGameServerId());
                if (gameServerMapping != null) {
                    InnerUserOffline userOffline = new InnerUserOffline();
                    userOffline.setAccount(account);
                    userOffline.setClientSessionId(userMapping.getClientSocketSession().getUid());
                    gameServerMapping.writeAndFlush(userOffline);
                }

                log.info("玩家离线：{}, {}, {}", account, clientSessionId, userMapping);
                userMapping.setChooseRoleId(0);
                userMapping.setGameServerId(0);
                userMapping.setCrossServerId(0);
                userMapping.setClientSocketSession(null);
            });
        }

        userMapping.setClientSocketSession(clientSession);
        userMapping.send2Client(req);
    }

}