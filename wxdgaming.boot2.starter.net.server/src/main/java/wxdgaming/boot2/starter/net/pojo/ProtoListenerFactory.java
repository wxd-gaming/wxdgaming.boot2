package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.ann.InitEvent;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.IClientWebSocketStringListener;
import wxdgaming.boot2.starter.net.server.IServerWebSocketStringListener;

import java.util.List;
import java.util.function.Supplier;

/**
 * 派发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 20:01
 **/
@Slf4j
@Getter
@Service
public class ProtoListenerFactory extends HoldApplicationContext {

    /** 相当于用 read and copy write方式作为线程安全性 */
    ProtoListenerContent protoListenerContent = null;
    IServerWebSocketStringListener serverWebSocketStringListener = null;
    IClientWebSocketStringListener clientWebSocketStringListener = null;
    ProtoUnknownMessageEvent protoUnknownMessageEvent = null;
    List<ServerProtoFilter> serverProtoFilters;
    List<ClientProtoFilter> clientProtoFilters;

    @Order(6)
    public void init(InitEvent initEvent) {
        protoListenerContent = new ProtoListenerContent(getApplicationContextProvider());
        serverWebSocketStringListener = getApplicationContextProvider().instance(IServerWebSocketStringListener.class);
        clientWebSocketStringListener = getApplicationContextProvider().instance(IClientWebSocketStringListener.class);
        protoUnknownMessageEvent = getApplicationContextProvider().instance(ProtoUnknownMessageEvent.class);
        serverProtoFilters = getApplicationContextProvider().classWithSuperStream(ServerProtoFilter.class).toList();
        clientProtoFilters = getApplicationContextProvider().classWithSuperStream(ClientProtoFilter.class).toList();
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        return protoListenerContent.messageId(pojoClass);
    }

    /** 这里是由netty的work线程触发 */
    public void dispatch(SocketSession socketSession, int messageId, byte[] data) {
        dispatch(socketSession, messageId, data, null);
    }

    public void dispatch(SocketSession socketSession, int messageId, byte[] data, Supplier<String> queueSupplier) {
        ProtoMapping mapping = protoListenerContent.getMappingMap().get(messageId);
        if (mapping == null) {
            if (protoUnknownMessageEvent != null) {
                protoUnknownMessageEvent.onUnknownMessageEvent(socketSession, messageId, data);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("收到消息：{} msgId={} - 未找到映射", socketSession, messageId);
                }
            }
            return;
        }
        ProtoEvent protoEvent = new ProtoEvent(getApplicationContextProvider(), mapping, socketSession, messageId, data);
        dispatch(socketSession, protoEvent, queueSupplier);
    }

    public void dispatch(SocketSession socketSession, ProtoEvent protoEvent, Supplier<String> queueSupplier) {
        /*根据映射解析生成触发事件*/
        ProtoListenerTrigger protoListenerTrigger = new ProtoListenerTrigger(protoEvent);
        boolean allMatch;
        if (socketSession.getType() == SocketSession.Type.server) {
            allMatch = serverProtoFilters.stream()
                    .filter(filter -> filter.localPort() == 0 || filter.localPort() == socketSession.getLocalPort())
                    .allMatch(filter -> filter.doFilter(protoListenerTrigger));
        } else {
            allMatch = clientProtoFilters.stream()
                    .filter(filter -> filter.localPort() == 0 || filter.localPort() == socketSession.getLocalPort())
                    .allMatch(filter -> filter.doFilter(protoListenerTrigger));
        }
        if (!allMatch) {
            if (log.isDebugEnabled()) {
                log.debug("收到消息：{} msgId={}, {} - 被过滤器剔除无需执行", socketSession, protoEvent.getMessageId(), protoEvent.buildMessage());
            }
            return;
        }
        if (!protoEvent.getProtoMapping().protoRequest().ignoreQueue()) {
            if (StringUtils.isBlank(protoListenerTrigger.getQueueName())) {
                String queueName;
                if (queueSupplier != null) {
                    queueName = queueSupplier.get();
                } else {
                    // 这里可以根据hashCode 来进行分组，16个队列
                    queueName = "session-drive-" + socketSession.getUid() % 16;
                }
                protoListenerTrigger.setQueueName(queueName);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("收到消息：{} queue={}, msgId={}, {}", socketSession, protoListenerTrigger.getQueueName(), protoEvent.getMessageId(), protoEvent.buildMessage());
        }
        /*提交到对应的线程和队列*/
        protoListenerTrigger.submit();
    }

}
