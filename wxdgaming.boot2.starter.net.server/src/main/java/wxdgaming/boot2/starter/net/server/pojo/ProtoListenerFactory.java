package wxdgaming.boot2.starter.net.server.pojo;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.net.server.SocketSession;
import wxdgaming.boot2.starter.net.server.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.ann.ProtoRequest;
import wxdgaming.boot2.starter.net.server.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 派发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 20:01
 **/
@Slf4j
@Getter
@Singleton
public class ProtoListenerFactory {

    private final ConcurrentHashMap<Integer, ProtoMapping> mappingMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Class<? extends PojoBase>> messageId2MappingMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends PojoBase>, Integer> message2MappingMap = new ConcurrentHashMap<>();

    RunApplication lastRunApplication;

    public int register(Class<? extends PojoBase> pojoClass) {
        int hashcode = StringUtils.hashcode(pojoClass.getName());
        message2MappingMap.put(pojoClass, hashcode);
        Class<? extends PojoBase> old = messageId2MappingMap.putIfAbsent(hashcode, pojoClass);
        if (old != null && !Objects.equals(old.getName(), pojoClass.getName())) {
            throw new RuntimeException("重复注册消息id: %s %s".formatted(hashcode, pojoClass));
        }
        return hashcode;
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        Integer hashcode = message2MappingMap.get(pojoClass);
        if (hashcode == null) {
            return register(pojoClass);
        }
        return hashcode;
    }

    public Class<? extends PojoBase> message(int messageId) {
        Class<? extends PojoBase> hashcode = messageId2MappingMap.get(messageId);
        if (hashcode == null) {
            throw new RuntimeException("未注册消息id: %s".formatted(messageId));
        }
        return hashcode;
    }

    @Init
    @Sort(6)
    public void init(RunApplication runApplication) {
        lastRunApplication = runApplication;
        runApplication.getReflectContext()
                .withMethodAnnotated(ProtoRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    ProtoRequest methodRequestMapping = AnnUtil.ann(method, ProtoRequest.class);

                    Class<? extends PojoBase> pojoClass = findPojoClass(method);
                    if (pojoClass == null) {
                        throw new RuntimeException("未找到消息类: %s".formatted(method));
                    }
                    int messageId = messageId(pojoClass);

                    ProtoMapping mapping = new ProtoMapping(methodRequestMapping, messageId, pojoClass, ins, method);

                    ProtoMapping old = mappingMap.put(messageId, mapping);
                    if (old != null && !Objects.equals(old.ins().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s, old = %s - new = %s"
                                .formatted(
                                        messageId,
                                        old.ins().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("proto listener messageId: {} handler: {}", messageId, ins.getClass());
                });
    }

    public Class<? extends PojoBase> findPojoClass(Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (clazz.isAssignableFrom(PojoBase.class)) {
                    return (Class<? extends PojoBase>) clazz;
                }
            }
        }
        return null;
    }

    public void dispatch(SocketSession socketSession, int messageId, byte[] data) {
        ProtoMapping mapping = mappingMap.get(messageId);
        if (mapping == null) {
            throw new RuntimeException("未找到消息id: %s".formatted(messageId));
        }
        mapping.invoke(lastRunApplication, socketSession, messageId, data);
    }

}
