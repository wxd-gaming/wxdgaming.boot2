package wxdgaming.boot2.starter.net.module.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.SocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * rpc 触发 事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 09:15
 **/
@Slf4j
public class RpcListenerTrigger extends ExecutorEvent {

    private final RpcMapping rpcMapping;
    private final RpcService rpcService;
    private final ApplicationContextProvider applicationContextProvider;
    private final SocketSession socketSession;
    private final long rpcId;
    private final JSONObject paramObject;

    public RpcListenerTrigger(RpcMapping rpcMapping,
                              RpcService rpcService,
                              ApplicationContextProvider applicationContextProvider,
                              SocketSession socketSession,
                              long rpcId,
                              JSONObject paramObject) {
        this.rpcMapping = rpcMapping;
        this.rpcService = rpcService;
        this.applicationContextProvider = applicationContextProvider;
        this.socketSession = socketSession;
        this.rpcId = rpcId;
        this.paramObject = paramObject;
    }

    @Override public String getStack() {
        return "RpcListenerTrigger: %s; %s.%s()".formatted(
                rpcMapping.path(),
                rpcMapping.providerMethod().getInstance().getClass().getName(),
                rpcMapping.providerMethod().getMethod().getName()
        );
    }

    @Override public void onEvent() {
        try {
            Object invoke = rpcMapping.providerMethod().invoke(injectorParameters());
            if (rpcMapping.providerMethod().getMethod().getReturnType() == void.class) {
                invoke = null;
            }
            if (rpcId > 0) {
                RunResult ret;
                if (invoke instanceof RunResult runResult) {
                    ret = runResult;
                } else {
                    ret = RunResult.ok();
                    if (invoke != null) {
                        ret.data(invoke);
                    }
                }
                rpcService.response(socketSession, rpcId, ret);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, rpcMapping.path(), paramObject, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.fail(500, "server error"));
            }
        }
    }

    public Object[] injectorParameters() {
        Parameter[] parameters = rpcMapping.providerMethod().getMethod().getParameters();
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
            } else if (JSONObject.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(paramObject);
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
            {
                /*实现注入*/
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    params[i] = applicationContextProvider.getBean(qualifier, parameterType);
                    continue;
                }
            }
            {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    String name = requestParam.value();
                    Object o;
                    try {
                        if (name.startsWith("${") && name.endsWith("}")) {
                            o = FastJsonUtil.getNestedValue(paramObject, name.substring(2, name.length() - 1), parameterizedType);
                        } else {
                            o = paramObject.getObject(name, parameterizedType);
                        }
                        if (o == null && StringUtils.isNotBlank(requestParam.defaultValue())) {
                            o = FastJsonUtil.parse(requestParam.defaultValue(), parameterizedType);
                        }
                    } catch (Exception e) {
                        throw Throw.of("param 参数：" + name, e);
                    }
                    if (requestParam.required() && o == null) {
                        throw new RuntimeException("param:" + name + " is null");
                    }
                    params[i] = o;
                    continue;
                }
            }

            {
                RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                if (requestBody != null) {
                    Object o = null;
                    if (!paramObject.isEmpty()) {
                        o = paramObject.toJavaObject(parameterType);
                    }
                    if (requestBody.required() && o == null) {
                        throw new RuntimeException("body is null");
                    }
                    params[i] = o;
                    continue;
                }
            }
        }
        return params;
    }

}
