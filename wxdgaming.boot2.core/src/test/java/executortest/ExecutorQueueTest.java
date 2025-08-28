package executortest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorQueue;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 队列测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-30 15:30
 **/
@Slf4j
public class ExecutorQueueTest {

    @Test
    public void abortPolicy() {
        AtomicInteger count = new AtomicInteger(0);
        ExecutorServicePlatform executorServicePlatform = ExecutorFactory.create("t", 10);
        ExecutorQueue executorQueue = new ExecutorQueue("t", executorServicePlatform, 10, 5, QueuePolicyConst.AbortPolicy);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            try {
                executorQueue.execute(() -> {
                    // log.info("执行任务 {}", finalI);
                    count.incrementAndGet();
                });
            } catch (Exception ignored) {}
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        log.info("abortPolicy count:{}", count.get());
    }

    @Test
    public void discardPolicy() {
        AtomicInteger count = new AtomicInteger(0);
        ExecutorServicePlatform executorServicePlatform = ExecutorFactory.create("t", 10);
        ExecutorQueue executorQueue = new ExecutorQueue("t", executorServicePlatform, 10, 5, QueuePolicyConst.DiscardPolicy);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            executorQueue.execute(() -> {
                // log.info("执行任务 {}", finalI);
                count.incrementAndGet();
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        log.info("discardPolicy count:{}", count.get());
    }

    @Test
    public void waitPolicy() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        ExecutorServicePlatform executorServicePlatform = ExecutorFactory.create("t", 10);
        ExecutorQueue executorQueue = new ExecutorQueue("t", executorServicePlatform, 10, 5, QueuePolicyConst.WaitPolicy);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            executorQueue.execute(() -> {
                // log.info("执行任务 {}", finalI);
                count.incrementAndGet();
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        log.info("waitPolicy count:{}", count.get());
    }

}
