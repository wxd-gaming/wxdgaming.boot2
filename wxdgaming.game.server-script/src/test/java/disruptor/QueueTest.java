package disruptor;

import com.lmax.disruptor.RingBuffer;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueTest {

    static int threadCount = 10;
    static final int capacity = (int) Math.pow(2, 22);
    static long start = 0;

    static void start() {
        start = System.nanoTime();
    }

    static void end(String prefix) {
        System.out.printf("%12s, %d 线程 读取: %d 次 cost: % 6.2f ms%n", prefix, threadCount, capacity, (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void arrayQueue() throws InterruptedException {
        ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(capacity);
        start();
        int threadCapacity = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCapacity * threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < threadCapacity; j++) {
                    queue.add(j);
                }
            });
            Thread.ofPlatform().start(() -> {
                for (int k = 0; k < threadCapacity; k++) {
                    try {
                        queue.take();
                        countDownLatch.countDown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        countDownLatch.await();
        end("arrayQueue");
    }

    @Test
    @RepeatedTest(10)
    public void linkedQueue() throws InterruptedException {
        LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>(capacity);
        start();
        int threadCapacity = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCapacity * threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < threadCapacity; j++) {
                    queue.add(j);
                }
            });
            Thread.ofPlatform().start(() -> {
                for (int k = 0; k < threadCapacity; k++) {
                    try {
                        queue.take();
                        countDownLatch.countDown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        countDownLatch.await();
        end("linkedQueue");
    }

    // @Test
    // @RepeatedTest(10)
    public void ringQueue() throws InterruptedException {
        RingQueue<Object> queue = new RingQueue<>(capacity);
        start();
        int threadCapacity = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCapacity * threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < threadCapacity; j++) {
                    queue.add(j);
                }
            });
            Thread.ofPlatform().start(() -> {
                for (int k = 0; k < threadCapacity; k++) {
                    queue.take();
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        end("ringQueue");
    }

    // @Test
    // @RepeatedTest(10)
    public void ringQueue2() throws InterruptedException {
        RingBlockingQueue<Object> queue = new RingBlockingQueue<>(capacity);
        start();
        int threadCapacity = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCapacity * threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < threadCapacity; j++) {
                    try {
                        queue.add(j);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Thread.ofPlatform().start(() -> {
                for (int k = 0; k < threadCapacity; k++) {
                    try {
                        queue.take();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        end("ringQueue2");
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

    @Test
    @RepeatedTest(10)
    public void ringBuffer() throws InterruptedException {
        RingBuffer<Event> queue = RingBuffer.createSingleProducer(new EventFactory(), capacity);
        start();
        int threadCapacity = capacity / threadCount;
        CountDownLatch countDownLatch = new CountDownLatch(threadCapacity * threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread.ofPlatform().start(() -> {
                for (int j = 0; j < threadCapacity; j++) {
                    long next = queue.next();
                    queue.get(next).setValue(String.valueOf(j));
                    queue.publish(next);
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        end("ringBuffer");
    }

}
