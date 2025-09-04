package wxdgaming.boot2.starter.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 09:48
 **/
@Slf4j
@Service
public class EventService extends HoldApplicationContext {

    Map<String, List<ApplicationContextProvider.ProviderMethod>> eventMap = Map.of();

    public EventService() {

    }

    @Init
    public void init() {
        log.info("EventService init");
        Map<String, List<ApplicationContextProvider.ProviderMethod>> tmpEventMap = new HashMap<>();
        getApplicationContextProvider().withMethodAssignableFrom(Event.class).forEach(method -> {
            Class<?> parameterType = method.getMethod().getParameterTypes()[0];
            String parameterTypeName = parameterType.getName();
            tmpEventMap.computeIfAbsent(parameterTypeName, k -> new ArrayList<>()).add(method);
        });
        eventMap = tmpEventMap;
    }

    /**
     * 抛出事件，但是如果执行遇到异常会中断
     * <p>如果事件执行需要先后顺序 {@link org.springframework.core.annotation.Order}
     */
    public void postEvent(Event event) {
        List<ApplicationContextProvider.ProviderMethod> providerMethods = eventMap.get(event.getClass().getName());
        if (providerMethods != null) {
            for (ApplicationContextProvider.ProviderMethod providerMethod : providerMethods) {
                providerMethod.invoke(event);
            }
        }
    }

    /**
     * 抛出事件，如果遇到异常会继续执行
     * <p>如果事件执行需要先后顺序 {@link org.springframework.core.annotation.Order}
     */
    public void postEventIgnoreException(Event event) {
        List<ApplicationContextProvider.ProviderMethod> providerMethods = eventMap.get(event.getClass().getName());
        if (providerMethods != null) {
            for (ApplicationContextProvider.ProviderMethod providerMethod : providerMethods) {
                try {
                    providerMethod.invoke(event);
                } catch (Throwable throwable) {
                    log.error("执行方法异常：{}-{}", providerMethod.getBean().getClass(), providerMethod.getMethod().getName(), throwable);
                }
            }
        }
    }

}
