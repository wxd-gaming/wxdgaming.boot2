package wxdgaming.game.gateway.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.pojo.ProtoUnknownMessageEvent;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.inner.ServiceType;

/**
 * 未知消息监听
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 20:28
 **/
@Slf4j
@Singleton
public class ReqProtoUnknownMessageEvent implements ProtoUnknownMessageEvent {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    @Inject
    public ReqProtoUnknownMessageEvent(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }

    @Override public void onUnknownMessageEvent(SocketSession socketSession, int messageId, byte[] messages) {
        /* 需要转发 */
        Integer gameServerId = socketSession.bindData("gameServerId");
        if (gameServerId == null) {
            /* 找不到服务监听 */
            log.warn("请求消息找不到转发的服务器：{}, msgId={}", socketSession, messageId);
            return;
        }
        ServerMapping serverMapping = dataCenterService.getServiceMappings().get(ServiceType.GAME, gameServerId);
        if (serverMapping == null) {
            /* 找不到服务监听 */
            log.warn("请求消息找不到转发的服务器：{},sid={}, msgId={}", socketSession, gameServerId, messageId);
            return;
        }
        if (!serverMapping.getMessageIds().contains(messageId)) {
            /* 找不到服务监听 */
            log.warn("请求消息转发的服务器：{},sid={}, msgId={} 不接受此消息监听", socketSession, gameServerId, messageId);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("转发消息:{}, {}", socketSession, messageId);
        }
        String account = socketSession.bindData("account");
        serverMapping.forwardMessage(socketSession.getUid(), messageId, messages, req -> {
            req.getKvBeansMap().put("account", account);
        });
    }
}
