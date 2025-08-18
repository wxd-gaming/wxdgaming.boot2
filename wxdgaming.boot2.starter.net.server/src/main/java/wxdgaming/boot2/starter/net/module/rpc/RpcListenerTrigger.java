package wxdgaming.boot2.starter.net.module.rpc;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Injector;
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
    private final RunApplication runApplication;
    private final SocketSession socketSession;
    private final long rpcId;
    private final JSONObject paramObject;

    public RpcListenerTrigger(RpcMapping rpcMapping,
                              RpcService rpcService,
                              RunApplication runApplication,
                              SocketSession socketSession,
                              long rpcId,
                              JSONObject paramObject) {
        this.rpcMapping = rpcMapping;
        this.rpcService = rpcService;
        this.runApplication = runApplication;
        this.socketSession = socketSession;
        this.rpcId = rpcId;
        this.paramObject = paramObject;
    }

    @Override public String getStack() {
        return "RpcListenerTrigger: %s; %s.%s()".formatted(
                rpcMapping.path(),
                rpcMapping.javassistProxy().getInstance().getClass().getName(),
                rpcMapping.javassistProxy().getMethod().getName()
        );
    }

    @Override public void onEvent() {
        try {
            Object invoke = rpcMapping.javassistProxy().proxyInvoke(injectorParameters());
            if (rpcMapping.javassistProxy().getMethod().getReturnType() == void.class) {
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
        Parameter[] parameters = rpcMapping.javassistProxy().getMethod().getParameters();
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
            } else if (JSONObject.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(paramObject);
                continue;
            }
            /*实现注入*/
            {
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    params[i] = BootConfig.getIns().value(value, parameterizedType);
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
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    String name = requestParam.value();
                    Object o;
                    try {
                        if (requestParam.nestedPath()) {
                            o = FastJsonUtil.getNestedValue(paramObject, name, parameterizedType);
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
                    if (o == null && StringUtils.isNotBlank(requestBody.defaultValue())) {
                        o = FastJsonUtil.parse(requestBody.defaultValue(), parameterType);
                    }
                    if (requestBody.required() && o == null) {
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
