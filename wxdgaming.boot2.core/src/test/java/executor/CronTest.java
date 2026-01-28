package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;
import wxdgaming.boot2.core.executor.AbstractExecutorService;
import wxdgaming.boot2.core.executor.CancelHolding;
import wxdgaming.boot2.core.executor.ExecutorServicePlatform;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.executor.RunnableQueue;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * cron 表达式调试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 14:23
 **/
@Slf4j
public class CronTest {

    @Test
    public void cron() {
        CronExpression cronExpression = CronExpression.parse("0 0 0 * * 1");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 10; i++) {
            now = cronExpression.next(now);
            System.out.println(now);
            if (now == null) break;
        }
    }

    @Test
    public void cron2() {
        AbstractExecutorService cronService = new ExecutorServicePlatform("cron", 1, 500, QueuePolicyConst.AbortPolicy);
        AtomicInteger taskIdFactory = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            final int taskId = taskIdFactory.incrementAndGet();
            cronService.addCronJob("*/5 * * ? * *", () -> {
                log.info("taskId:{}, Thread:{} {}", taskId, Thread.currentThread().getName(), "执行任务");
            });
        }
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(10));
    }

    @Test
    public void cron3() {
        AbstractExecutorService cronService = new ExecutorServicePlatform("cron", 5, 500, QueuePolicyConst.AbortPolicy);
        AtomicInteger taskIdFactory = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            final int taskId = taskIdFactory.incrementAndGet();
            CronQueue cronQueue = new CronQueue(taskId);
            cronQueue.cancelHolding = cronService.addCronJob("*/5 * * ? * *", cronQueue);
        }
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(10));
    }

    public static class CronQueue implements Runnable, RunnableQueue {

        private CancelHolding cancelHolding = null;
        private final int taskId;
        private int runCount = 0;

        public CronQueue(int taskId) {
            this.taskId = taskId;
        }

        @Override public String getQueueName() {
            return "abc";
        }

        @Override public void run() {
            log.info("taskId:{}, Thread:{} {}", taskId, Thread.currentThread().getName(), "执行任务");
            runCount++;
            if (runCount >= taskId) {
                cancelHolding.cancel();
            }
        }
    }

}
