package wxdgaming.game.server.script.inner;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.game.message.inner.ReqForwardMessage;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;

import java.util.List;
import java.util.Map;

/**
 * 内置服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 10:53
 **/
@Slf4j
@Singleton
public class InnerService extends HoldRunApplication {

    private final ProtoListenerFactory protoListenerFactory;
    private final DataCenterService dataCenterService;

    @Inject
    public InnerService(ProtoListenerFactory protoListenerFactory, DataCenterService dataCenterService) {
        this.protoListenerFactory = protoListenerFactory;
        this.dataCenterService = dataCenterService;
    }

    ReqForwardMessage buildForwardMessage(PojoBase message) {
        int messageId = protoListenerFactory.messageId(message.getClass());
        byte[] messageBytes = message.encode();
        ReqForwardMessage req = new ReqForwardMessage();
        req.setMessageId(messageId);
        req.setMessages(messageBytes);
        return req;
    }

    public void forwardMessage(Player player, PojoBase message) {
        ReqForwardMessage req = buildForwardMessage(message);
        req.getRids().add(player.getUid());
        req.getSessionIds().add(player.getSessionId());
        player.getSocketSession().write(req);
    }

    public void forwardMessage(ServiceType serviceType, long sessionId, PojoBase message) {
        ReqForwardMessage req = buildForwardMessage(message);
        req.getSessionIds().add(sessionId);
        SocketSession socketSession = dataCenterService.getServiceSocketSessionMapping().get(serviceType, sessionId);
        if (socketSession == null) {
            log.error("转发消息失败，未找到服务：{}，sessionId：{}", serviceType, sessionId);
            return;
        }
        socketSession.write(req);
    }

    public void forwardMessage(ServiceType serviceType, List<Long> playerIds, PojoBase message) {
        ReqForwardMessage req = buildForwardMessage(message);
        req.getRids().addAll(playerIds);
        Map<Long, SocketSession> longSocketSessionMap = dataCenterService.getServiceSocketSessionMapping().get(serviceType);
        if (longSocketSessionMap == null) {
            log.error("转发消息失败{}<UNK>", serviceType);
            return;
        }
        /*因为不知道玩家在那个网关，所以全部转发*/
        longSocketSessionMap.values().forEach(session -> session.write(req));
    }

}
