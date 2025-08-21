package wxdgaming.game.server.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.boot2.starter.net.pojo.ProtoMapping;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.server.bean.role.Player;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-21 14:55
 **/
@Getter
@SuperBuilder
public class InnerForwardEvent extends ProtoEvent {

    private final ClientSessionMapping clientSessionMapping;
    private final Player player;
    final InnerForwardMessage forwardMessage;
    final String clientIp;
    final long clientSessionId;

    public InnerForwardEvent(ApplicationContextProvider applicationContextProvider, ProtoMapping protoMapping,
                             SocketSession socketSession,
                             int messageId, byte[] bytes,
                             ClientSessionMapping clientSessionMapping,
                             Player player,
                             InnerForwardMessage forwardMessage,
                             String clientIp, long clientSessionId) {
        super(applicationContextProvider, protoMapping, socketSession, messageId, bytes);
        this.clientSessionMapping = clientSessionMapping;
        this.player = player;
        this.forwardMessage = forwardMessage;
        this.clientIp = clientIp;
        this.clientSessionId = clientSessionId;
    }

}
