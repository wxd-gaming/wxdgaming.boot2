package wxdgaming.boot2.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Injector;
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
import wxdgaming.boot2.starter.net.SocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * rpc 触发 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:15
 **/
@Slf4j
public class RpcListenerTrigger extends Event {

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

    @Override public String getTaskInfoString() {
        return "RpcListenerTrigger: " + rpcMapping.path() + "; " + rpcMapping.ins().getClass().getName() + "." + rpcMapping.method().getName() + "()";
    }

    @Override public void onEvent() {
        try {
            Object invoke = rpcMapping.method().invoke(rpcMapping.ins(), injectorParameters(runApplication, socketSession, paramObject));
            if (rpcMapping.method().getReturnType() == void.class) {
                invoke = null;
            }
            if (rpcId > 0) {
                RunResult data = RunResult.ok();
                if (invoke != null) {
                    data.data(invoke);
                }
                rpcService.response(socketSession, rpcId, data);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, rpcMapping.path(), paramObject, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.error(500, "server error"));
            }
        }
    }

    public Object[] injectorParameters(RunApplication runApplication, SocketSession socketSession, JSONObject paramObject) {
        Parameter[] parameters = rpcMapping.method().getParameters();
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
                } else if (JSONObject.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(paramObject);
                    continue;
                }
                /*实现注入*/
                {
                    Value value = parameter.getAnnotation(Value.class);
                    if (value != null) {
                        params[i] = BootConfig.getIns().value(value, clazz);
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
                            o = paramObject.getObject(name, clazz);
                            if (o == null && StringUtils.isNotBlank(param.defaultValue())) {
                                o = FastJsonUtil.parse(param.defaultValue(), type);
                            }
                        } catch (Exception e) {
                            throw Throw.of("param 参数：" + name, e);
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
                        if (!paramObject.isEmpty()) {
                            o = paramObject.toJavaObject(clazz);
                        }
                        if (o == null && StringUtils.isNotBlank(body.defaultValue())) {
                            o = FastJsonUtil.parse(body.defaultValue(), clazz);
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
