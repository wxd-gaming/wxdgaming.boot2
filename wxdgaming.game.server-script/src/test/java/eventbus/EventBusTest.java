package eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;


public class EventBusTest {
    // 定义一个事件类
    record StringEvent(String message) {

    }

    // 定义一个事件类
    record NumberEvent(Number message) {

    }

    // 定义一个订阅者类
    @Slf4j
    static class MessageSubscriber {

        @Subscribe
        public void stringEventHandle(StringEvent event) {
            log.info("Received StringEvent: " + event.message());
        }

        @Subscribe
        public void numberEventHandle(NumberEvent event) {
            log.info("Received NumberEvent: " + event.message());
        }

    }

    public static void main(String[] args) {
        // 创建 EventBus 实例
        EventBus eventBus = new EventBus();

        // 创建订阅者实例
        MessageSubscriber subscriber = new MessageSubscriber();

        // 注册订阅者到 EventBus
        eventBus.register(subscriber);
        // 发布事件
        eventBus.post(new StringEvent("Hello, EventBus!"));
        // 发布事件
        eventBus.post(new NumberEvent(1));
        // 取消订阅者注册
        eventBus.unregister(subscriber);

        // 再次发布事件，由于订阅者已取消注册，不会有输出
        eventBus.post(new StringEvent("This message won't be received."));
        // 发布事件
        eventBus.post(new NumberEvent(9));
    }

}
