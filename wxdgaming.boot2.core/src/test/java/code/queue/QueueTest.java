package code.queue;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueTest {

    static final int capacity = 1000000;
    static long start = 0;
    static int threadCount = 10;

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
        CountDownLatch countDownLatch = new CountDownLatch(capacity);
        int threadCapacity = capacity / threadCount;
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
        CountDownLatch countDownLatch = new CountDownLatch(capacity);
        int threadCapacity = capacity / threadCount;
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

    @Test
    @RepeatedTest(10)
    public void ringQueue() throws InterruptedException {
        RingQueue<Object> queue = new RingQueue<>(capacity);
        start();
        CountDownLatch countDownLatch = new CountDownLatch(capacity);
        int threadCapacity = capacity / threadCount;
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

}
