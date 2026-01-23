package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

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
        CronService cronService = new CronService(1);
        AtomicInteger taskIdFactory = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            final int taskId = taskIdFactory.incrementAndGet();
            cronService.addJob("*/5 * * ? * *", () -> {
                log.info("taskId:{}, Thread:{} {}", taskId, Thread.currentThread().getName(), "执行任务");
            });
        }
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(10));
    }

    @Test
    public void cron3() {
        CronService cronService = new CronService(5);
        AtomicInteger taskIdFactory = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            final int taskId = taskIdFactory.incrementAndGet();
            cronService.addJob("*/5 * * ? * *", new CronQueue(taskId));
        }
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(10));
    }

    public static class CronQueue implements Runnable, RunnableQueue {

        private final int taskId;

        public CronQueue(int taskId) {
            this.taskId = taskId;
        }

        @Override public String queueName() {
            return "abc";
        }

        @Override public void run() {
            log.info("taskId:{}, Thread:{} {}", taskId, Thread.currentThread().getName(), "执行任务");
        }
    }

}
