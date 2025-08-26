package code.queue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.lang.DiffTimeRecord;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class RingQueueTest {

    public void l1() {
        LinkedList<Integer> s1 = new LinkedList<>();
        s1.add(1);
    }

    public static void main(String[] args) {
        RingQueue<String> ringQueue = new RingQueue<>(10);
        // for (int i = 0; i < 10; i++) {
        //     ringQueue.add("1");
        // }
        // JSON.toJSONString(ringQueue, SerializerFeature.WriteClassName);
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            Thread.ofPlatform().start(() -> {
                while (true) {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(300));
                    ringQueue.add(String.valueOf(1));
                    System.out.println("添加" + finalI);
                }
            });
        }


        Thread.ofPlatform().start(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                ringQueue.take();
                System.out.println("==========取出1");
            }
        });

        Thread.ofPlatform().start(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                ringQueue.take();
                System.out.println("==========取出2");
            }
        });

    }

    public DiffTimeRecord newDiffTimeRecord() {
        return DiffTimeRecord.start(DiffTimeRecord.IntervalConvertConst.US);
    }

    @Test
    @Order(7)
    public void RingQueuePutMulti() throws Exception {
        int capacity = 300_0000;
        RingQueue<String> ringBuffer = new RingQueue<>(capacity);
        DiffTimeRecord diffTime = newDiffTimeRecord();
        int threadCount = 20;
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
        System.out.println("RingQueue 需要填充: " + (i1 * threadCount) + ", 填充: " + ringBuffer.size() + ", " + diffTime.totalInterval().toString());
    }

}
