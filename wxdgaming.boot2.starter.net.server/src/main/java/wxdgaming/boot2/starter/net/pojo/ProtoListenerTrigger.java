package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.reflect.GuiceBeanProvider;
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
    private final RunApplication runApplication;
    private final SocketSession socketSession;
    private final int messageId;
    private final byte[] bytes;
    private PojoBase pojoBase;

    public ProtoListenerTrigger(ProtoMapping protoMapping, RunApplication runApplication, SocketSession socketSession, int messageId, byte[] bytes) {
        this.protoMapping = protoMapping;
        this.runApplication = runApplication;
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
            if (GuiceBeanProvider.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication.getGuiceBeanProvider());
                continue;
            } else if (RunApplication.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication);
                continue;
            } else if (Injector.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication.getInjector());
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
            params[i] = runApplication.getInstanceByParameter(parameter);
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
