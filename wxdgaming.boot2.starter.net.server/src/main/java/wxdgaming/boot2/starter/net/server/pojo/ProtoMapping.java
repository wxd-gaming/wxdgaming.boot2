package wxdgaming.boot2.starter.net.server.pojo;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Qualifier;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.starter.net.server.SocketSession;
import wxdgaming.boot2.starter.net.server.ann.ProtoRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Slf4j
public record ProtoMapping(
        ProtoRequest protoRequest,
        int messageId,
        Class<? extends PojoBase> pojoClass,
        Object ins,
        Method method) {

    public void invoke(RunApplication runApplication, SocketSession socketSession, int messageId, byte[] bytes) {
        try {
            PojoBase pojoBase = pojoClass.getDeclaredConstructor().newInstance();
            pojoBase.decode(bytes);
            method.invoke(ins, injectorParameters(runApplication, socketSession, pojoBase));
        } catch (Throwable e) {
            log.error("messageId={}, {}", messageId, pojoClass.getSimpleName(), e);
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, SocketSession socketSession, PojoBase pojoBase) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (GuiceReflectContext.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(runApplication.getReflectContext());
                    continue;
                } else if (RunApplication.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(runApplication);
                    continue;
                } else if (Injector.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(runApplication.getInjector());
                    continue;
                } else if (SocketSession.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(socketSession);
                    continue;
                } else if (pojoBase.getClass().isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(socketSession);
                    continue;
                }
                /*实现注入*/
                {
                    Value value = parameter.getAnnotation(Value.class);
                    if (value != null) {
                        String name = value.name();
                        Object o = BootConfig.getIns().getObject(name, clazz);
                        if (o == null && !value.defaultValue().isBlank()) {
                            o = value.defaultValue();
                        }
                        if (value.required() && o == null) {
                            throw new RuntimeException("value:" + name + " is null");
                        }
                        params[i] = o;
                        continue;
                    }
                }

                try {
                    params[i] = runApplication.getInjector().getInstance(clazz);
                } catch (Exception e) {
                    Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                    if (qualifier != null && qualifier.required()) {
                        throw new RuntimeException("bean:" + clazz.getName() + " is not bind");
                    }
                }
            }
        }
        return params;
    }

}
