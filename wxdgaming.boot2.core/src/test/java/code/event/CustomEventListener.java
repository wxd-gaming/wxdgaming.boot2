package code.event;

import lombok.Getter;
import wxdgaming.boot2.core.reflect.ReflectContext;

import java.util.EventObject;

/**
 * 事件监听器接口
 *
 * @param <E>
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-22 19:38
 */
@Getter
public abstract class CustomEventListener<E extends EventObject> {

    private final Class<? extends EventObject> eventClass;

    public CustomEventListener() {
        eventClass = ReflectContext.getTClass(this.getClass());
    }

    public abstract void onCustomEvent(E event);

}
