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
import wxdgaming.game.message.inner.ReqForwardMessage;
import wxdgaming.game.message.role.ResLogin;

import java.util.List;

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
        ReqForwardMessage forwardMessage = ThreadContext.context("forwardMessage");
        List<Long> sessionIds = forwardMessage.getSessionIds();
        Long sessionId = sessionIds.getFirst();
        SocketSession clientSession = dataCenterService.getClientSession(sessionId);
        String account = req.getAccount();
        clientSession.bindData("account", account);
        clientSession.write(req);
        UserMapping userMapping = dataCenterService.getUserMappings().computeIfAbsent(account, k -> new UserMapping());
        if (userMapping.getSocketSession() != null) {
            userMapping.getSocketSession().close("被顶号登录");
        }
        userMapping.setSocketSession(clientSession);
    }

}