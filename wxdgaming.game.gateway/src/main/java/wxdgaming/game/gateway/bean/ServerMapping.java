package wxdgaming.game.gateway.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.message.inner.InnerForwardMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 服务映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 10:24
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class ServerMapping extends ObjectBase {

    private int gid;
    private int mainSid;
    private List<Integer> sid;
    private SocketSession session;
    private String ip;
    private int port;
    private int webPort;
    private Set<Integer> messageIds = new HashSet<>();

    public void forwardMessage(long clientSessionId, int messageId, byte[] messages) {
        forwardMessage(clientSessionId, messageId, messages, null);
    }

    public void forwardMessage(long clientSessionId, int messageId, byte[] messages, Consumer<InnerForwardMessage> callback) {
        InnerForwardMessage innerForwardMessage = new InnerForwardMessage();
        innerForwardMessage.setMessageId(messageId);
        innerForwardMessage.setMessages(messages);
        innerForwardMessage.getKvBeansMap().put("clientSessionId", String.valueOf(clientSessionId));
        if (callback != null) {
            callback.accept(innerForwardMessage);
        }
        if (session == null || !session.isOpen()) {
            log.debug("发送消息 连接不可用：clientSessionId={}, msgId={}, {}", clientSessionId, messageId, innerForwardMessage);
            return;
        }
        session.writeAndFlush(innerForwardMessage);
    }

    @Override public String toString() {
        return "ServerMapping{mainSid=%d, sid=%s, ip='%s', port=%d, webPort=%d, messageIds=%s, session=%s}"
                .formatted(mainSid, sid, ip, port, webPort, messageIds, session);
    }
}
