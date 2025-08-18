package code.event.impl;

import code.event.EventListener;
import code.event.EventObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 字符串监听
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-22 17:30
 **/
@Slf4j
public abstract class StringEventListener extends EventListener<StringEvent> {

    @Override public Class<? extends EventObject> getEventClass() {
        Class<? extends EventObject> eventClass = super.getEventClass();
        if (eventClass.equals(Object.class)) {
            return StringEvent.class;
        }
        return eventClass;
    }

}
