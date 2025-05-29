package wxdgaming.game.gateway.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.ReqForwardMessage;
import wxdgaming.game.message.role.ResChooseRole;

import java.util.List;

/**
 * 选择角色响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ResChooseRoleHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    @Inject
    public ResChooseRoleHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }

    /** 选择角色响应 */
    @ProtoRequest
    public void resChooseRole(SocketSession socketSession, ResChooseRole req) {
        ReqForwardMessage forwardMessage = ThreadContext.context("forwardMessage");
        List<Long> sessionIds = forwardMessage.getSessionIds();
        for (Long sessionId : sessionIds) {
            SocketSession clientSession = dataCenterService.getClientSession(sessionId);
            clientSession.write(req);
        }
    }

}