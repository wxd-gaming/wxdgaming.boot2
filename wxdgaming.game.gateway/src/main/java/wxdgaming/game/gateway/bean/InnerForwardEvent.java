package wxdgaming.game.gateway.bean;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoEvent;
import wxdgaming.boot2.starter.net.pojo.ProtoMapping;
import wxdgaming.game.message.inner.InnerForwardMessage;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-21 14:55
 **/
@Getter
@SuperBuilder
public class InnerForwardEvent extends ProtoEvent {

    final UserMapping userMapping;
    final InnerForwardMessage forwardMessage;

    public InnerForwardEvent(ApplicationContextProvider applicationContextProvider, ProtoMapping protoMapping,
                             SocketSession socketSession,
                             int messageId, byte[] bytes,
                             UserMapping userMapping,
                             InnerForwardMessage forwardMessage) {
        super(applicationContextProvider, protoMapping, socketSession, messageId, bytes);
        this.userMapping = userMapping;
        this.forwardMessage = forwardMessage;
    }

}
