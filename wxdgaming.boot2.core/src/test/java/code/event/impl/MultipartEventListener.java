package code.event.impl;

import code.event.EventListener;
import code.event.EventObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 多参数事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-23 15:09
 **/
@Slf4j
public abstract class MultipartEventListener<E extends MultipartEvent> extends EventListener<E> {

    @Override public Class<? extends EventObject> getEventClass() {
        Class<? extends EventObject> eventClass = super.getEventClass();
        if (eventClass.equals(Object.class)) {
            return MultipartEvent.class;
        }
        return eventClass;
    }

}
