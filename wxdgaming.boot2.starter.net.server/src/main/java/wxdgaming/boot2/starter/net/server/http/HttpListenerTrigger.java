package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Injector;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.*;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.core.threading.Event;
import wxdgaming.boot2.core.threading.ThreadContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * http 监听 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:29
 **/
@Slf4j
public class HttpListenerTrigger extends Event {

    private final HttpMapping httpMapping;
    private final RunApplication runApplication;
    private final HttpContext httpContext;

    public HttpListenerTrigger(HttpMapping httpMapping, RunApplication runApplication, HttpContext httpContext) {
        super(httpMapping.method());
        this.httpMapping = httpMapping;
        this.runApplication = runApplication;
        this.httpContext = httpContext;
    }

    @Override public String getTaskInfoString() {
        return "HttpListenerTrigger: " + httpMapping.path() + "; " + httpMapping.ins().getClass().getName() + "." + httpMapping.method().getName() + "()";
    }

    @Override public void onEvent() throws Exception {
        if (StringUtils.isNotBlank(httpMapping.httpRequest().method())) {
            /*请求格式处理，比如必须是get请求，或者必须是post请求*/
            if (!httpMapping.httpRequest().method().equalsIgnoreCase(httpContext.getRequest().httpMethod().name())) {
                httpContext.getResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
                httpContext.getResponse().response("method not allowed");
                return;
            }
        }
        try {
            Object invoke = httpMapping.method().invoke(httpMapping.ins(), injectorParameters(runApplication, httpContext));
            if (invoke != null) {
                httpContext.getResponse().response(invoke);
            } else {
                httpContext.getResponse().response("");
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            StringBuilder stringBuilder = httpContext.showLog();
            stringBuilder
                    .append("=============================================异常================================================")
                    .append("\n")
                    .append(Throw.ofString(e))
                    .append("\n=============================================结束================================================")
                    .append("\n");
            log.error(
                    "{}",
                    stringBuilder
            );
            stringBuilder.setLength(0);
            httpContext.getResponse().setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            httpContext.getResponse().response(RunResult.error("server error " + e.getMessage()));
        } finally {
            httpContext.close();
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, HttpContext context) {
        Parameter[] parameters = httpMapping.method().getParameters();
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
                        Object valued = BootConfig.getIns().value(value, clazz);
                        params[i] = clazz.cast(valued);
                        continue;
                    }
                }
                {
                    ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                    if (threadParam != null) {
                        params[i] = ThreadContext.context(threadParam, clazz);
                        continue;
                    }
                }
                {
                    Param param = parameter.getAnnotation(Param.class);
                    if (param != null) {
                        String name = param.path();
                        Object o;
                        try {
                            o = context.getRequest().getReqParams().getObject(name, clazz);
                            if (o == null && StringUtils.isNotBlank(param.defaultValue())) {
                                o = FastJsonUtil.parse(param.defaultValue(), type);
                            }
                        } catch (Exception e) {
                            throw Throw.of("参数：" + name, e);
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
                        if (o == null && StringUtils.isNotBlank(body.defaultValue())) {
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
                    params[i] = runApplication.getInstance(clazz);
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
