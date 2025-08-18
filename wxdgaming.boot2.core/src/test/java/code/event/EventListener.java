package code.event;

import lombok.Getter;
import wxdgaming.boot2.core.reflect.ReflectProvider;


/**
 * 事件监听器接口
 *
 * @param <E>
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-22 19:38
 */
@Getter
public abstract class EventListener<E extends EventObject> {

    private final Class<? extends EventObject> eventClass;

    @SuppressWarnings(value = {"rawtypes", "unchecked"})
    public EventListener() {
        Class tmp = Object.class;
        try {
            tmp = ReflectProvider.getTClass(this.getClass());
        } catch (Exception ignored) {
        }
        this.eventClass = tmp;
    }

    public abstract EventType eventType();

    public abstract void onEvent(E event);

}
