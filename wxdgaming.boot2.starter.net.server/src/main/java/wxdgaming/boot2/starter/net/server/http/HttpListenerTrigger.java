package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.*;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.chatset.json.FastJsonUtil;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.reflect.GuiceBeanProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.starter.net.ann.HttpPath;

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
public class HttpListenerTrigger extends ExecutorEvent {

    public static void execute(RunApplication runApplication, HttpMapping httpMapping, HttpContext httpContext, boolean showLog) {
        if (showLog) {
            StringBuilder stringBuilder = httpContext.showLog();
            log.info("{}", stringBuilder);
        }
        new HttpListenerTrigger(httpMapping, runApplication, httpContext).submit();
    }

    private final HttpMapping httpMapping;
    private final RunApplication runApplication;
    private final HttpContext httpContext;

    public HttpListenerTrigger(HttpMapping httpMapping, RunApplication runApplication, HttpContext httpContext) {
        super(httpMapping.javassistProxy().getMethod());
        this.httpMapping = httpMapping;
        this.runApplication = runApplication;
        this.httpContext = httpContext;
    }

    @Override public String getStack() {
        return "HttpListenerTrigger: %s; %s.%s()".formatted(
                httpMapping.path(),
                httpMapping.javassistProxy().getInstance().getClass().getName(),
                httpMapping.javassistProxy().getMethod().getName()
        );
    }

    @Override public void onEvent() throws Exception {
        if (StringUtils.isNotBlank(httpMapping.httpRequest().method())) {
            /*请求格式处理，比如必须是get请求，或者必须是post请求*/
            if (!httpMapping.httpRequest().method().equalsIgnoreCase(httpContext.getRequest().httpMethod().name())) {
                httpContext.getResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
                httpContext.getResponse().response(RunResult.fail("method not allowed"));
                return;
            }
        }
        try {
            ThreadContext.putContent("http-path", httpMapping.path());
            Object invoke = httpMapping.javassistProxy().proxyInvoke(injectorParameters(runApplication, httpContext));
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
            GlobalUtil.exception(stringBuilder.toString(), e);
            stringBuilder.setLength(0);
            httpContext.getResponse().setStatus(HttpResponseStatus.OK);
            httpContext.getResponse().response(RunResult.fail("server error " + e.getMessage()));
        } finally {
            httpContext.close();
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, HttpContext httpContext) {
        Parameter[] parameters = httpMapping.javassistProxy().getMethod().getParameters();
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
            } else if (HttpContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(httpContext);
                continue;
            } else if (HttpContext.Request.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(httpContext.getRequest());
                continue;
            } else if (HttpContext.Response.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(httpContext.getResponse());
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
            {
                ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                if (threadParam != null) {
                    params[i] = ThreadContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            {
                HttpPath httpPath = parameter.getAnnotation(HttpPath.class);
                if (httpPath != null) {
                    String string = httpContext.getRequest().getPathMatcherMap().get(httpPath.value());
                    AssertUtil.assertTrue(StringUtils.isNotBlank(string), "path param:" + httpPath.value() + " is null");
                    if (String.class.equals(parameterizedType))
                        params[i] = string;
                    else
                        params[i] = FastJsonUtil.parse(string, parameterizedType);
                    continue;
                }
            }
            {
                Param param = parameter.getAnnotation(Param.class);
                if (param != null) {
                    String name = param.path();
                    Object o;
                    try {

                        if (param.nestedPath()) {
                            o = FastJsonUtil.getNestedValue(httpContext.getRequest().getReqParams(), name, parameterizedType);
                        } else {
                            o = httpContext.getRequest().getReqParams().getObject(name, parameterizedType);
                        }
                        if (o == null && StringUtils.isNotBlank(param.defaultValue())) {
                            if (String.class.equals(parameterizedType))
                                o = param.defaultValue();
                            else
                                o = FastJsonUtil.parse(param.defaultValue(), parameterizedType);
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
                    if (StringUtils.isNotBlank(httpContext.getRequest().getReqContent())) {
                        o = httpContext.getRequest().getReqParams().toJavaObject(parameterType);
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
            params[i] = runApplication.getInstanceByParameter(parameter);
        }
        return params;
    }

}
