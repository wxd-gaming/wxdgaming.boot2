package wxdgaming.boot2.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.boot2.starter.net.SocketSession;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 事件触发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 08:45
 **/
@Slf4j
@Getter
public class ProtoListenerTrigger extends ExecutorEvent {

    private final ProtoMapping protoMapping;
    private final ApplicationContextProvider applicationContextProvider;
    private final SocketSession socketSession;
    private final int messageId;
    private final byte[] bytes;
    private PojoBase pojoBase;

    public ProtoListenerTrigger(ProtoMapping protoMapping, ApplicationContextProvider applicationContextProvider, SocketSession socketSession, int messageId, byte[] bytes) {
        this.protoMapping = protoMapping;
        this.applicationContextProvider = applicationContextProvider;
        this.socketSession = socketSession;
        this.messageId = messageId;
        this.bytes = bytes;
    }

    @Override public String getStack() {
        return "ProtoListenerTrigger %s messageId=%s, %s".formatted(socketSession, messageId, getPojoBase());
    }

    @Override public void onEvent() throws Exception {
        try {
            protoMapping.javassistProxy().proxyInvoke(injectorParameters());
        } catch (Throwable e) {
            log.error("{} messageId={}, {}", socketSession, messageId, getPojoBase(), e);
        }
    }

    public Object[] injectorParameters() {
        Parameter[] parameters = protoMapping.javassistProxy().getMethod().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (ApplicationContextProvider.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(applicationContextProvider);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(applicationContextProvider.getApplicationContext());
                continue;
            } else if (SocketSession.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(socketSession);
                continue;
            } else if (protoMapping.pojoClass().isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(getPojoBase());
                continue;
            }
            /*实现注入*/
            {
                ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                if (threadParam != null) {
                    params[i] = ThreadContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            /*实现注入*/
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                String name = qualifier.value();
                if (StringUtils.isBlank(name))
                    params[i] = applicationContextProvider.getBean(parameterType);
                else
                    params[i] = applicationContextProvider.getBean(name);
            }
        }
        return params;
    }

    public PojoBase getPojoBase() {
        if (pojoBase == null) {
            pojoBase = ReflectProvider.newInstance(protoMapping.pojoClass());
            pojoBase.decode(bytes);
        }
        return pojoBase;
    }
}
