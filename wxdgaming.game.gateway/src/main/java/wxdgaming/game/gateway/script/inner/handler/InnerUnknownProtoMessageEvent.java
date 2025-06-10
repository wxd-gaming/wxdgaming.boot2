package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoUnknownMessageEvent;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.ServiceType;

/**
 * 前端发送消息网关无监听，转发到游戏或者社交进程
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 20:28
 **/
@Slf4j
@Singleton
public class InnerUnknownProtoMessageEvent implements ProtoUnknownMessageEvent {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    @Inject
    public InnerUnknownProtoMessageEvent(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }

    @Override public void onUnknownMessageEvent(SocketSession socketSession, int messageId, byte[] messages) {
        Class<? extends PojoBase> messageType = listenerFactory.getProtoListenerContent().getMessageId2MappingMap().get(messageId);
        forwardGameServer(socketSession, messageId, messages, messageType);
        forwardChartServer(socketSession, messageId, messages, messageType);

    }

    void forwardGameServer(SocketSession socketSession, int messageId, byte[] messages, Class<? extends PojoBase> messageType) {
        /* 需要转发 */
        Integer gameServerId = socketSession.bindData("gameServerId");
        if (gameServerId == null) {
            /* 找不到服务监听 */
            log.warn("请求消息找不到转发的服务器：{}, msg={}({})", socketSession, messageId, messageType);
            return;
        }
        ServiceType serviceType = ServiceType.GAME;
        ServerMapping serverMapping = dataCenterService.getGameServiceMappings().get(gameServerId);
        if (serverMapping == null) {
            /* 找不到服务监听 */
            log.warn("请求消息找不到转发的服务器：{},sid={}, msg={}({})", socketSession, gameServerId, messageId, messageType);
            return;
        }
        if (!serverMapping.getMessageIds().contains(messageId)) {
            /* 找不到服务监听 */
            log.warn("请求消息转发的服务器：{},sid={}, msg={}({}) 不接受此消息监听", socketSession, gameServerId, messageId, messageType);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("转发消息:{}, {}, {}({})", serviceType, socketSession, messageId, messageType);
        }
        String account = socketSession.bindData("account");
        serverMapping.forwardMessage(socketSession.getUid(), messageId, messages, req -> {
            req.getKvBeansMap().put("account", account);
        });
    }

    void forwardChartServer(SocketSession socketSession, int messageId, byte[] messages, Class<? extends PojoBase> messageType) {
        ServiceType serviceType = ServiceType.CHAT;
        ServerMapping chartServiceMapping = dataCenterService.getChartServiceMapping();
        if (chartServiceMapping.getSession() == null) {
            return;
        }
        if (!chartServiceMapping.getMessageIds().contains(messageId)) {
            /* 找不到服务监听 */
            log.debug("请求消息转发的服务器：{}, 社交进程 msg={}({}) 不接受此消息监听", socketSession, messageId, messageType);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("转发消息:{}, {}, {}({})", serviceType, socketSession, messageId, messageType);
        }
        String account = socketSession.bindData("account");
        chartServiceMapping.forwardMessage(socketSession.getUid(), messageId, messages, req -> {
            req.getKvBeansMap().put("account", account);
        });
    }

}
