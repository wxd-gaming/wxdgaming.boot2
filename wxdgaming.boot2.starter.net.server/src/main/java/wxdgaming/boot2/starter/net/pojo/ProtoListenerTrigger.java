package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.reflect.InstanceMethodProvider;

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
        InstanceMethodProvider providerMethod = protoEvent.getProtoMapping().providerMethod();
        return providerMethod.getInstance().getClass() + "#" + providerMethod.getMethod().getName();
    }

    @Override public void onEvent() throws Exception {
        try {
            protoEvent.getProtoMapping().providerMethod().invoke(protoEvent);
        } catch (Throwable e) {
            log.error(
                    """
                            执行消息请求异常
                            session={},
                            method={},
                            messageId={},
                            message={}
                            """,
                    protoEvent.getSocketSession(),
                    protoEvent.getProtoMapping().providerMethod().getMethod().toString(),
                    protoEvent.getMessageId(),
                    protoEvent.buildMessage(),
                    e
            );
        }
    }

}
