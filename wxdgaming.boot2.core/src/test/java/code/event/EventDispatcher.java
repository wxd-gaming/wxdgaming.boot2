package code.event;

import lombok.extern.slf4j.Slf4j;

import java.util.EventObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * 事件派发
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-22 17:16
 **/
@Slf4j
public class EventDispatcher {

    @SuppressWarnings("rawtypes")
    private final ConcurrentHashMap<Class, CopyOnWriteArrayList<CustomEventListener<? extends EventObject>>> listeners = new ConcurrentHashMap<>();

    public void addListener(CustomEventListener<? extends EventObject> listener) {
        listeners.computeIfAbsent(listener.getEventClass(), k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <E extends EventObject> void removeListener(CustomEventListener<E> listener) {
        listeners.computeIfAbsent(listener.getEventClass(), k -> new CopyOnWriteArrayList<>()).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends EventObject> void dispatchEvent(E event) {
        Executor executor = null;


        listeners.getOrDefault(event.getClass(), new CopyOnWriteArrayList<>())
                .forEach(listener -> {
                    CustomEventListener<E> eventListener = (CustomEventListener<E>) listener;
                    executor.execute(() -> eventListener.onCustomEvent(event));

                });
    }

}
