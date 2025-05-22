package code.event;

import code.event.impl.StringEvent;
import code.event.impl.StringEventListener;

public class EventDispatcherTest {

    public static void main(String[] args) {

        EventDispatcher eventDispatcher = new EventDispatcher();
        StringEventListener listener = new StringEventListener();
        eventDispatcher.addListener(listener);

        eventDispatcher.dispatchEvent(new StringEvent(listener, "test"));

    }

}
