package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Body;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.ann.Qualifier;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.starter.net.server.ann.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Slf4j
public record HttpMapping(HttpRequest httpRequest, String path, Object ins, Method method) {

    public void invoke(RunApplication runApplication, HttpContext context) throws Exception {
        Object invoke = method.invoke(ins, injectorParameters(runApplication, context));
        if (invoke != null) {
            context.getResponse().response(invoke);
        } else {
            context.getResponse().response("");
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, HttpContext context) {
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
                } else if (HttpContext.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(context);
                    continue;
                } else if (HttpContext.Request.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(context.getRequest());
                    continue;
                } else if (HttpContext.Response.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(context.getResponse());
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

                {
                    Param param = parameter.getAnnotation(Param.class);
                    if (param != null) {
                        String name = param.name();
                        Object o = context.getRequest().getReqParams().getObject(name, clazz);
                        if (o == null && !param.defaultValue().isBlank()) {
                            o = param.defaultValue();
                        }
                        if (param.required() && o == null) {
                            throw new RuntimeException("param:" + name + " is null");
                        }
                        params[i] = o;
                        continue;
                    }
                }

                {
                    Body body = parameter.getAnnotation(Body.class);
                    if (body != null) {
                        Object o = null;
                        if (StringUtils.isNotBlank(context.getRequest().getReqContent())) {
                            o = context.getRequest().getReqParams().toJavaObject(clazz);
                        }
                        if (o == null && !body.defaultValue().isBlank()) {
                            o = body.defaultValue();
                        }
                        if (body.required() && o == null) {
                            throw new RuntimeException("body is null");
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
