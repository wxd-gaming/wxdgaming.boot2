package wxdgaming.game.server.script.inner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.DataCenterService;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 内置服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-28 10:53
 **/
@Slf4j
@Service
public class InnerService extends HoldApplicationContext {

    private final DataCenterService dataCenterService;
    private final ClientSessionService clientSessionService;

    public InnerService(DataCenterService dataCenterService, ClientSessionService clientSessionService) {
        this.dataCenterService = dataCenterService;
        this.clientSessionService = clientSessionService;
    }

    InnerForwardMessage buildForwardMessage(PojoBase message) {
        int messageId = message.msgId();
        byte[] messageBytes = message.encode();
        InnerForwardMessage req = new InnerForwardMessage();
        req.setMessageId(messageId);
        req.setMessages(messageBytes);
        return req;
    }


    public void forwardMessage(SocketSession socketSession, long clientSessionId, PojoBase message, Consumer<InnerForwardMessage> callback) {
        InnerForwardMessage req = buildForwardMessage(message);
        req.getSessionIds().add(clientSessionId);
        if (callback != null) {
            callback.accept(req);
        }
        if (log.isDebugEnabled()) {
            log.debug("发送消息：clientSessionId={}, msgId={}, {}", clientSessionId, message.msgId(), message);
        }
        socketSession.write(req);
    }

    public void forwardMessage(ServiceType serviceType, Collection<Long> playerIds, PojoBase message) {
        InnerForwardMessage req = buildForwardMessage(message);
        req.getRids().addAll(playerIds);
        Map<Integer, SocketSession> longSocketSessionMap = clientSessionService.getServiceSocketSessionMapping().get(serviceType);
        if (longSocketSessionMap == null) {
            log.error("转发消息失败{} {}", serviceType, message);
            return;
        }
        /*因为不知道玩家在那个网关，所以全部转发*/
        longSocketSessionMap.values().forEach(session -> session.write(req));
    }

}
