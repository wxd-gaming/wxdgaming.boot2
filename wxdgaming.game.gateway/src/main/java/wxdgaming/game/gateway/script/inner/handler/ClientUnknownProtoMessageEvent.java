package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoUnknownMessageEvent;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.bean.UserMapping;
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
public class ClientUnknownProtoMessageEvent implements ProtoUnknownMessageEvent {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    @Inject
    public ClientUnknownProtoMessageEvent(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }

    @Override public void onUnknownMessageEvent(SocketSession socketSession, int messageId, byte[] messages) {

        Class<? extends PojoBase> messageType = listenerFactory.getProtoListenerContent().getMessageId2MappingMap().get(messageId);
        /* 需要转发 */
        UserMapping userMapping = socketSession.bindData("userMapping");
        if (userMapping == null) {
            /* 找不到服务监听 */
            log.warn("需要转发的消息找不到映射：{}, msg={}({})", socketSession, messageId, messageType);
            return;
        }

        forwardGameServer(userMapping, messageId, messages, messageType);
        forwardChartServer(userMapping, messageId, messages, messageType);

    }

    void forwardGameServer(UserMapping userMapping, int messageId, byte[] messages, Class<? extends PojoBase> messageType) {
        ServerMapping serverMapping = dataCenterService.getGameServiceMappings().get(userMapping.getChooseServerId());
        if (serverMapping == null) {
            /* 找不到服务监听 */
            log.warn("转发消息找不到游戏服务器：{}, msg={}({})", userMapping, messageId, messageType);
            return;
        }
        if (!serverMapping.getMessageIds().contains(messageId)) {
            /* 找不到服务监听 */
            if (log.isDebugEnabled()) {
                log.debug("转发消息游戏服：{}, msg={}({}) 不接受此消息监听", userMapping, messageId, messageType);
            }
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("转发消息游戏服: {}, {}({})", userMapping, messageId, messageType);
        }
        serverMapping.forwardMessage(userMapping.clientSessionId(), messageId, messages, req -> {
            req.getKvBeansMap().put("account", userMapping.getAccount());
        });
    }

    void forwardChartServer(UserMapping userMapping, int messageId, byte[] messages, Class<? extends PojoBase> messageType) {
        ServerMapping chartServiceMapping = dataCenterService.getChartServiceMapping();
        if (chartServiceMapping.getSession() == null) {
            return;
        }
        if (!chartServiceMapping.getMessageIds().contains(messageId)) {
            /* 找不到服务监听 */
            log.debug("转发消息社交进程：{}, msg={}({}) 不接受此消息监听", userMapping, messageId, messageType);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("转发消息社交进程: {}, {}({})", userMapping, messageId, messageType);
        }
        chartServiceMapping.forwardMessage(userMapping.clientSessionId(), messageId, messages, req -> {
            req.getKvBeansMap().put("account", userMapping.getAccount());
        });
    }

}
