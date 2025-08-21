package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;

/**
 * 事件触发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 08:45
 **/
@Slf4j
@Getter
public class ProtoListenerTrigger extends ExecutorEvent {

    private final ProtoEvent protoEvent;

    public ProtoListenerTrigger(ProtoEvent protoEvent) {
        this.protoEvent = protoEvent;
    }

    @Override public String getStack() {
        return "ProtoListenerTrigger %s messageId=%s, %s".formatted(protoEvent.getSocketSession(), protoEvent.getMessageId(), protoEvent.buildMessage());
    }

    @Override public void onEvent() throws Exception {
        try {
            protoEvent.getProtoMapping().javassistProxy().proxyInvoke(new Object[]{protoEvent});
        } catch (Throwable e) {
            log.error("{}", getStack(), e);
        }
    }

}
