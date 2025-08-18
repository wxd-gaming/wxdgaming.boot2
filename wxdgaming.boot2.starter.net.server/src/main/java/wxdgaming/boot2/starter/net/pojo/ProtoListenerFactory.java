package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.chatset.StringUtils;
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
@Singleton
public class ProtoListenerFactory {

    /** 相当于用 read and copy write方式作为线程安全性 */
    ProtoListenerContent protoListenerContent = null;
    IServerWebSocketStringListener serverWebSocketStringListener = null;
    IClientWebSocketStringListener clientWebSocketStringListener = null;
    ProtoUnknownMessageEvent protoUnknownMessageEvent = null;
    List<ServerProtoFilter> serverProtoFilters;
    List<ClientProtoFilter> clientProtoFilters;

    @Init
    @Order(6)
    public void init(RunApplication runApplication) {
        protoListenerContent = new ProtoListenerContent(runApplication);
        serverWebSocketStringListener = runApplication.classWithSuper(IServerWebSocketStringListener.class).findFirst().orElse(null);
        clientWebSocketStringListener = runApplication.classWithSuper(IClientWebSocketStringListener.class).findFirst().orElse(null);
        protoUnknownMessageEvent = runApplication.classWithSuper(ProtoUnknownMessageEvent.class).findFirst().orElse(null);
        serverProtoFilters = runApplication.classWithSuper(ServerProtoFilter.class).toList();
        clientProtoFilters = runApplication.classWithSuper(ClientProtoFilter.class).toList();
    }

    public RunApplication getRunApplication() {
        return protoListenerContent.getRunApplication();
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
        /*根据映射解析生成触发事件*/
        ProtoListenerTrigger protoListenerTrigger = new ProtoListenerTrigger(mapping, protoListenerContent.getRunApplication(), socketSession, messageId, data);
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
                log.debug("收到消息：{} msgId={}, {} - 被过滤器剔除无需执行", socketSession, messageId, protoListenerTrigger.getPojoBase());
            }
            return;
        }
        if (!mapping.protoRequest().ignoreQueue()) {
            if (StringUtils.isBlank(protoListenerTrigger.queueName())) {
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
            log.debug("收到消息：{} queue={}, msgId={}, {}", socketSession, protoListenerTrigger.queueName(), messageId, protoListenerTrigger.getPojoBase());
        }
        /*提交到对应的线程和队列*/
        protoListenerTrigger.submit();
    }

}
