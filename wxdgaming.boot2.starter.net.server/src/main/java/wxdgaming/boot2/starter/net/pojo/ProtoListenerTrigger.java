package wxdgaming.boot2.starter.net.pojo;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Qualifier;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.starter.net.SocketSession;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 事件触发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:45
 **/
@Slf4j
public class ProtoListenerTrigger extends Event {

    private final ProtoMapping protoMapping;
    private final RunApplication runApplication;
    private final SocketSession socketSession;
    private final int messageId;
    private final byte[] bytes;

    public ProtoListenerTrigger(ProtoMapping protoMapping, RunApplication runApplication, SocketSession socketSession, int messageId, byte[] bytes) {
        super(protoMapping.method());
        this.protoMapping = protoMapping;
        this.runApplication = runApplication;
        this.socketSession = socketSession;
        this.messageId = messageId;
        this.bytes = bytes;
    }

    @Override public void onEvent() throws Exception {
        try {
            if (log.isDebugEnabled()) {
                log.debug("收到消息：{} {} {}", socketSession, messageId, protoMapping.pojoClass().getSimpleName());
            }
            PojoBase pojoBase = protoMapping.pojoClass().getDeclaredConstructor().newInstance();
            pojoBase.decode(bytes);
            protoMapping.method().invoke(protoMapping.ins(), injectorParameters(runApplication, socketSession, pojoBase));
        } catch (Throwable e) {
            log.error("{} messageId={}, {}", socketSession, messageId, protoMapping.pojoClass().getSimpleName(), e);
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, SocketSession socketSession, PojoBase pojoBase) {
        Parameter[] parameters = protoMapping.method().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (GuiceReflectContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(runApplication.getReflectContext());
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
            } else if (pojoBase.getClass().isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(pojoBase);
                continue;
            }
            /*实现注入*/
            {
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    Object valued = BootConfig.getIns().value(value, parameterizedType);
                    params[i] = parameterType.cast(valued);
                    continue;
                }
            }

            try {
                params[i] = runApplication.getInstance(parameterType);
            } catch (Exception e) {
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier != null && qualifier.required()) {
                    throw new RuntimeException("bean:" + parameterType.getName() + " is not bind");
                }
            }
        }
        return params;
    }
}
