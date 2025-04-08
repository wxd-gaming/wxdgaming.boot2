package eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;


public class AsyncEventBusTest {

    // 定义一个事件类
    static record StringEvent(Channel channel, String message) {

    }

    // 定义一个事件类
    static record NumberEvent(Channel channel, Number message) {

    }

    // 定义一个订阅者类
    @Slf4j
    static class StringEventSubscriber {

        @Subscribe
        public void stringEventHandle(StringEvent event) {
            log.info("Received StringEvent: " + event.message());
        }

    }

    // 定义一个订阅者类
    @Slf4j
    static class NumberEventSubscriber {


        @Subscribe
        public void numberEventHandle(NumberEvent event) {
            log.info("Received NumberEvent: " + event.message());
        }
    }

    public static void main(String[] args) {
        ReentrantLock rlock = new ReentrantLock();
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // 创建 EventBus 实例
        AsyncEventBus eventBus = new AsyncEventBus(executorService);

        // 注册订阅者到 EventBus
        eventBus.register(new StringEventSubscriber());
        // 注册订阅者到 EventBus
        eventBus.register(new NumberEventSubscriber());
        // 发布事件
        eventBus.post(new StringEvent(null, "Hello, EventBus!"));
        // 发布事件
        eventBus.post(new NumberEvent(null, 1));
        // 发布事件
        eventBus.post(new StringEvent(null, "Hello, EventBus!"));
        // 发布事件
        eventBus.post(new NumberEvent(null, 1));
        // 发布事件
        eventBus.post(new StringEvent(null, "Hello, EventBus!"));
        // 发布事件
        eventBus.post(new NumberEvent(null, 1));
    }

}
