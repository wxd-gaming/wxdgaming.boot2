package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.AbstractEventRunnable;
import wxdgaming.boot2.core.executor.AbstractMethodRunnable;
import wxdgaming.boot2.core.reflect.InstanceMethodProvider;

/**
 * 事件触发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 08:45
 **/
@Slf4j
@Getter
public class ProtoListenerTrigger extends AbstractMethodRunnable {

    private final ProtoEvent protoEvent;

    public ProtoListenerTrigger(ProtoEvent protoEvent) {
        super(protoEvent.getProtoMapping().providerMethod().getMethod());
        this.protoEvent = protoEvent;
        InstanceMethodProvider providerMethod = protoEvent.getProtoMapping().providerMethod();
        this.sourceLine = providerMethod.getInstance().getClass() + "#" + providerMethod.getMethod().getName();
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
