package code.event;

import code.event.impl.MultipartEvent;
import code.event.impl.MultipartEventListener;
import code.event.impl.StringEvent;
import code.event.impl.StringEventListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDispatcherTest {

    public static void main(String[] args) {

        EventDispatcher eventDispatcher = new EventDispatcher();

        StringEventListener stringEventListener = new StringEventListener() {

            @Override public EventType eventType() {
                return EventTypeConst.ServerInit;
            }

            @Override public void onEvent(StringEvent event) {
                log.info("{}", event);
            }
        };
        MultipartEventListener<MultipartEvent> multipartEventListener = new MultipartEventListener<MultipartEvent>() {

            @Override public EventType eventType() {
                return EventTypeConst.MultipartInit;
            }

            @Override public void onEvent(MultipartEvent event) {
                log.info("{}", event);
            }

        };

        final EventType eventType = EventType.build("hello");


        eventDispatcher.addListener(stringEventListener);
        eventDispatcher.addListener(multipartEventListener);
        eventDispatcher.addListener(eventType, multipartEventListener);

        eventDispatcher.dispatchEvent(EventTypeConst.ServerInit, new StringEvent("test"));

        eventDispatcher.dispatchEvent(eventType, MultipartEvent.builder().message("test").number(1).build());

        eventDispatcher.dispatchEvent(EventTypeConst.MultipartInit, MultipartEvent.builder().message("test").number(2).build());

        eventDispatcher.removeListener(stringEventListener);
    }

}
