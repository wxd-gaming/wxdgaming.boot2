package disruptor;

import com.lmax.disruptor.RingBuffer;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.core.format.data.Data2Size;
import wxdgaming.boot2.core.lang.DiffTime;

import java.util.concurrent.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QueueTest {

    @Order(1)
    public void q1() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
        queue.add("1");
        long ca = 5000_0000;
        System.out.println(Long.MAX_VALUE / ca);
        System.out.println(TimeUnit.SECONDS.toHours(Long.MAX_VALUE / ca));
        System.out.println(TimeUnit.SECONDS.toDays(Long.MAX_VALUE / ca));
        System.out.println(TimeUnit.SECONDS.toDays(Long.MAX_VALUE / ca) / 365);
    }

    @Getter
    @Setter
    public static class Event {

        private String value;

    }

    public static class EventFactory implements com.lmax.disruptor.EventFactory<Event> {

        @Override
        public Event newInstance() {
            return new Event();
        }

    }

    @Order(2)
    public void queueMemory() {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(capacity);
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(capacity);
        ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

        RingBuffer<Event> ringBuffer = RingBuffer.createSingleProducer(new EventFactory(), capacity);

        for (int i = 0; i < capacity; i++) {
            arrayBlockingQueue.add(String.valueOf(i));
            linkedBlockingQueue.add(String.valueOf(i));
            concurrentLinkedQueue.add(String.valueOf(i));
            ringBuffer.get(i).setValue(String.valueOf(i));
        }
        System.out.println("ArrayBlockingQueue 内存占用：" + Data2Size.totalSizes0(arrayBlockingQueue));
        System.out.println("LinkedBlockingQueue 内存占用：" + Data2Size.totalSizes0(linkedBlockingQueue));
        System.out.println("ConcurrentLinkedQueue 内存占用：" + Data2Size.totalSizes0(concurrentLinkedQueue));
        System.out.println("RingBuffer 内存占用：" + Data2Size.totalSizes0(ringBuffer));
    }

    final int capacity = 1024 * 1024;
    final int threadCount = 10;

    @Test
    @Order(3)
    @RepeatedTest(10)
    public void ArrayBlockingQueuePut() throws Exception {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(capacity);
        DiffTime diffTime = new DiffTime();
        int i1 = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread.ofPlatform().start(() -> {
                for (int i2 = 0; i2 < i1; i2++) {
                    queue.add(finalI + " - " + String.valueOf(i2));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("ArrayBlockingQueue 需要填充: " + (i1 * threadCount) + ", 填充: " + queue.size() + ", 耗时: " + diffTime.diff() + " ms");
    }

    @Test
    @Order(4)
    @RepeatedTest(10)
    public void LinkedBlockingQueuePut() throws Exception {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(capacity);
        DiffTime diffTime = new DiffTime();
        int i1 = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread.ofPlatform().start(() -> {
                for (int i2 = 0; i2 < i1; i2++) {
                    queue.add(finalI + " - " + String.valueOf(i2));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("LinkedBlockingQueue 需要填充: " + (i1 * threadCount) + ", 填充: " + queue.size() + ", 耗时: " + diffTime.diff() + " ms");
    }

    @Test
    @Order(5)
    @RepeatedTest(10)
    public void ConcurrentLinkedQueuePut() throws Exception {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        DiffTime diffTime = new DiffTime();
        int i1 = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread.ofPlatform().start(() -> {
                for (int i2 = 0; i2 < i1; i2++) {
                    queue.add(finalI + " - " + String.valueOf(i2));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("ConcurrentLinkedQueue 需要填充: " + (i1 * threadCount) + ", 填充: " + queue.size() + ", 耗时: " + diffTime.diff() + " ms");
    }

    @Test
    @Order(6)
    @RepeatedTest(10)
    public void RingBufferPutMulti() throws Exception {
        RingBuffer<Event> ringBuffer = RingBuffer.createSingleProducer(new EventFactory(), capacity);
        DiffTime diffTime = new DiffTime();
        int i1 = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread.ofPlatform().start(() -> {
                for (int i2 = 0; i2 < i1; i2++) {
                    long next = ringBuffer.next();
                    ringBuffer.get(next).setValue(finalI + " - " + String.valueOf(i2));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("RingBuffer 需要填充: " + (i1 * threadCount) + ", 填充: " + ringBuffer.getBufferSize() + ", 耗时: " + diffTime.diff() + " ms");
    }


    @Test
    @Order(7)
    @RepeatedTest(10)
    public void RingQueuePutMulti() throws Exception {
        RingQueue<String> ringBuffer = new RingQueue<>(capacity);
        DiffTime diffTime = new DiffTime();
        int i1 = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread.ofPlatform().start(() -> {
                for (int i2 = 0; i2 < i1; i2++) {
                    ringBuffer.add(finalI + " - " + String.valueOf(i2));
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("RingQueue 需要填充: " + (i1 * threadCount) + ", 填充: " + ringBuffer.size() + ", 耗时: " + diffTime.diff() + " ms");
    }

}
