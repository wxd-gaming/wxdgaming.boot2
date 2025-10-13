package code.event;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件派发
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-22 17:16
 **/
@Slf4j
public class EventDispatcher {

    // @SuppressWarnings("rawtypes")
    private final ConcurrentTable<EventType, Class<? extends EventObject>, CopyOnWriteArrayList<EventListener<? extends EventObject>>> listeners = new ConcurrentTable<>();

    /** 获取事件类型监听器容器 */
    private CopyOnWriteArrayList<EventListener<? extends EventObject>> getListeners(EventType eventType, Class<? extends EventObject> eventData) {
        AssertUtil.isTrue(eventType != null, "eventType is null");
        AssertUtil.isTrue(eventData != null, "eventDataClass is null");
        return listeners.computeIfAbsent(eventType, eventData, k -> new CopyOnWriteArrayList<>());
    }


    /** 添加监听器 */
    public <E extends EventObject> void addListener(EventListener<E> listener) {
        addListener(listener.eventType(), listener);
    }

    /** 添加监听器 */
    public <E extends EventObject> void addListener(EventType eventType, EventListener<E> listener) {
        getListeners(eventType, listener.getEventClass()).add(listener);
    }

    /** 删除监听器 */
    public <E extends EventObject> void removeListener(EventListener<E> listener) {
        removeListener(listener.eventType(), listener);
    }

    /** 删除监听器 */
    public <E extends EventObject> void removeListener(EventType eventType, EventListener<E> listener) {
        getListeners(eventType, listener.getEventClass()).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends EventObject> void dispatchEvent(EventType eventType, E event) {
        Class<? extends EventObject> aClass = event.getClass();
        CopyOnWriteArrayList<EventListener<? extends EventObject>> orDefault = getListeners(eventType, aClass);
        for (EventListener<? extends EventObject> listener : orDefault) {
            EventListener<E> eventListener = (EventListener<E>) listener;
            eventListener.onEvent(event);
        }
    }

}
