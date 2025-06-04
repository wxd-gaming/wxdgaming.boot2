package wxdgaming.game.gateway.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
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
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
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
        final Long sessionId = sessionIds.getFirst();
        final String account = req.getAccount();
        SocketSession clientSession = dataCenterService.getClientSession(sessionId);
        clientSession.bindData("account", account);
        clientSession.write(req);

        UserMapping userMapping = dataCenterService.getUserMapping(account);
        userMapping.setChooseServerId(req.getSid());
        userMapping.setChooseServerSession(socketSession);
        if (userMapping.getClientSocketSession() != null && !Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
            userMapping.getClientSocketSession().close("被顶号登录");
        }
        if (userMapping.getClientSocketSession() == null || !Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
            clientSession.getChannel().closeFuture().addListener(future -> {
                if (!Objects.equals(userMapping.getClientSocketSession(), clientSession)) {
                    return;
                }
                InnerUserOffline userOffline = new InnerUserOffline();
                userOffline.setAccount(account);
                userOffline.setClientSessionId(sessionId);
                userMapping.getChooseServerSession().write(userOffline);
                log.info("玩家离线：{}, {}", account, sessionId);
            });
        }
        userMapping.setClientSocketSession(clientSession);
    }

}