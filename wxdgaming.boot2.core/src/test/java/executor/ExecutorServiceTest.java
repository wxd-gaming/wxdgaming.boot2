package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 14:48
 **/
@Slf4j
public class ExecutorServiceTest {

    @Test
    public void stack() {
        AbstractEventRunnable abstractEventRunnable = new AbstractEventRunnable() {
            @Override
            public void run() {
                log.debug("ddd: {}", ExecutorVO.threadLocal());
            }
        };
        System.out.println(abstractEventRunnable);
        System.out.println(new TestEventRunnable());
    }

    @Test
    public void t1() {
        AbstractExecutorService executorServicePlatform = new ExecutorServicePlatform("platform", 4, 1000, QueuePolicyConst.AbortPolicy);
        test(executorServicePlatform);
        AbstractExecutorService executorServiceVirtual = new ExecutorServiceVirtual("virtual", 4, 1000, QueuePolicyConst.AbortPolicy);
        test(executorServiceVirtual);
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(3));
    }

    public void test(AbstractExecutorService executorServicePlatform) {
        for (int i = 0; i < 10; i++) {
            executorServicePlatform.execute(new AbstractEventRunnable() {

                @Override public String queueName() {
                    return "ddd";
                }

                @Override public void run() {
                    System.out.println("1");
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                }
            });
            executorServicePlatform.execute(() -> {
                ExecutorVO executorVO = ExecutorVO.threadLocal();
                log.debug("ddd: {}", executorVO);
            });
            executorServicePlatform.execute(new Run1());
        }

        ScheduledRunnable.schedule(executorServicePlatform, () -> {
            log.debug("schedule");
        }, 1, TimeUnit.SECONDS);

        ScheduledRunnable.scheduleAtFixedRate(executorServicePlatform, new AbstractEventRunnable() {
            @Override public void run() {
                log.debug("scheduleAtFixedRate");
                ExecutorMonitor.threadContext().startWatch("scheduleAtFixedRate");
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                ExecutorMonitor.threadContext().stopWatch();
            }
        }, 0, 1, TimeUnit.SECONDS);

        ScheduledRunnable.scheduleWithFixedDelay(executorServicePlatform, () -> {
            log.debug("scheduleWithFixedDelay");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static class Run1 implements Runnable, RunnableQueue {

        @Override public String queueName() {
            return "abc";//RandomStringUtils.secure().next(4, true, true);
        }

        @Override
        public void run() {
            ExecutorVO executorVO = ExecutorVO.threadLocal();
            log.debug("ddd: {}", executorVO);
        }

    }

    public static class TestEventRunnable extends AbstractEventRunnable {

        public TestEventRunnable() {
            AbstractEventRunnable abstractEventRunnable = new AbstractEventRunnable() {
                @Override
                public void run() {
                    log.debug("ddd: {}", ExecutorVO.threadLocal());
                }
            };
            System.out.println(abstractEventRunnable);
        }

        @Override
        public void run() {
            log.debug("ddd: {}", ExecutorVO.threadLocal());
        }

    }

}
