package disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


public class DisruptorMultipleEventsExample {


    @Getter
    @Setter
    static class Event {

        Object data;

        @Override public String toString() {
            return String.valueOf(data);
        }
    }

    // 定义事件工厂
    static class EventFactory implements com.lmax.disruptor.EventFactory<Event> {
        @Override
        public Event newInstance() {
            return new Event(); // 这里由于有多种事件类型，我们在生产时再具体创建
        }
    }

    // 定义事件处理程序
    @Slf4j
    static class EventStringHandler implements com.lmax.disruptor.EventHandler<Event> {
        @Override
        public void onEvent(Event eventString, long sequence, boolean endOfBatch) {
            log.info("处理 EventString: " + eventString);
        }
    }

    @Slf4j
    static class EventString2Handler implements com.lmax.disruptor.EventHandler<Event> {
        @Override
        public void onEvent(Event eventString, long sequence, boolean endOfBatch) {
            log.info("处理 EventString2: " + eventString);
        }
    }


    // 定义事件处理程序
    @Slf4j
    static class EventNumberHandler implements com.lmax.disruptor.EventHandler<Event> {
        @Override
        public void onEvent(Event eventNumber, long sequence, boolean endOfBatch) {
            log.info("处理 EventNumber: " + eventNumber);
        }
    }

    // 定义生产者
    static class EventProducer {
        private final RingBuffer<Event> ringBuffer;

        public EventProducer(RingBuffer<Event> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        public void produceEvent(Object message) {
            long sequence = ringBuffer.next();
            try {
                Event event = ringBuffer.get(sequence);
                event.setData(message);
            } finally {
                ringBuffer.publish(sequence);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 定义环形缓冲区的大小，必须是 2 的幂
        int bufferSize = 1024;

        // 创建 Disruptor
        Disruptor<Event> disruptor = new Disruptor<>(
                new EventFactory(),
                bufferSize,
                DaemonThreadFactory.INSTANCE
        );

        // 这个位置是消费者
        disruptor.handleEventsWith(new EventStringHandler(), new EventString2Handler());

        // 启动 Disruptor
        disruptor.start();

        // 获取环形缓冲区
        RingBuffer<Event> ringBuffer = disruptor.getRingBuffer();

        // 创建多个生产者
        EventProducer producer1 = new EventProducer(ringBuffer);
        for (int i = 0; i < 5; i++) {
            producer1.produceEvent("producer1 - string " + i);
        }
        Thread.sleep(3000);
        // 关闭 Disruptor 和线程池
        disruptor.shutdown();
    }
}
