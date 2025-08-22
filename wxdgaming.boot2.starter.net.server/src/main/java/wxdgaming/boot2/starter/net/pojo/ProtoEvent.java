package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.net.SocketSession;

/**
 * proto消息事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-21 14:25
 **/
@Getter
@SuperBuilder
public class ProtoEvent {

    private final ApplicationContextProvider applicationContextProvider;
    private final ProtoMapping protoMapping;
    private final SocketSession socketSession;
    private final int messageId;
    private final byte[] bytes;
    private PojoBase protoMessage;
    private Object bindData;

    public ProtoEvent(ApplicationContextProvider applicationContextProvider, ProtoMapping protoMapping,
                      SocketSession socketSession, int messageId, byte[] bytes) {
        this.applicationContextProvider = applicationContextProvider;
        this.protoMapping = protoMapping;
        this.socketSession = socketSession;
        this.messageId = messageId;
        this.bytes = bytes;
    }

    public void bindData(Object bindData) {
        this.bindData = bindData;
    }

    public <R> R bindData() {
        return (R) bindData;
    }

    public <R extends PojoBase> R buildMessage() {
        if (protoMessage == null) {
            protoMessage = ReflectProvider.newInstance(protoMapping.pojoClass());
            protoMessage.decode(bytes);
        }
        return (R) protoMessage;
    }

}
