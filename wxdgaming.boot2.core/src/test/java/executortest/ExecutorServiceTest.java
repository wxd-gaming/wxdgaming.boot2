package executortest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import wxdgaming.boot2.core.executor.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ExecutorServiceTest {

    @BeforeEach
    void beforeEach() {
        if (ExecutorFactory.Lazy.instance != null) return;
        ExecutorProperties executorProperties = new ExecutorProperties();
        ExecutorConfig logic = new ExecutorConfig();
        logic.setCoreSize(12).setMaxQueueSize(500000).setWarnSize(500000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
        executorProperties.setLogic(logic);
        new ExecutorFactory(executorProperties);
    }


    public static void main(String[] args) {
        new ExecutorServiceTest().beforeEach();
        ExecutorServicePlatform executorServicePlatform = ExecutorFactory.create("4", 10, 100, 10, QueuePolicyConst.AbortPolicy);

        {
            ThreadContext.context().put("test", "myRunnable");
            executorServicePlatform.execute(new MyRunnable());
            MyRunnable myRunnable = new MyRunnable();
            executorServicePlatform.schedule(
                    myRunnable,
                    1,
                    TimeUnit.SECONDS
            );

            ThreadContext.cleanup();

        }

        executorServicePlatform.schedule(
                new MyRunnableQueue(),
                1,
                TimeUnit.SECONDS
        );

        MyTimerRunnable myTimerRunnable = new MyTimerRunnable();
        myTimerRunnable.scheduledFuture = executorServicePlatform.scheduleAtFixedRate(
                myTimerRunnable,
                1,
                1,
                TimeUnit.SECONDS
        );

        MyTimerRunnable2 myTimerRunnable2 = new MyTimerRunnable2();
        executorServicePlatform.scheduleWithFixedDelay(
                myTimerRunnable2,
                1,
                1,
                TimeUnit.SECONDS
        );

        MyTimerRunnable2Queue command = new MyTimerRunnable2Queue();
        command.scheduledFuture = executorServicePlatform.scheduleWithFixedDelay(
                command,
                1,
                1,
                TimeUnit.SECONDS
        );
    }

    private static class MyRunnable extends ExecutorEvent implements Runnable {

        public MyRunnable() {
        }

        @Override public void onEvent() throws Exception {
            log.info("1 {} {}", StackUtils.stack(), ThreadContext.context().get("test"));
            ThreadStopWatch.start(StackUtils.stack());
            ThreadStopWatch.stop();
            throw new RuntimeException("1");
        }
    }

    private static class MyRunnableQueue extends ExecutorEvent implements Runnable, IExecutorQueue {

        @Override public String getQueueName() {
            return "1";
        }

        @Override public void onEvent() throws Exception {
            log.info("1 {} {}", getStack(), ThreadContext.context().get("test"));
            ThreadStopWatch.start(StackUtils.stack());
            ThreadStopWatch.stop();
        }

    }

    private static class MyTimerRunnable implements Runnable {

        int runCount = 0;
        ScheduledFuture<?> scheduledFuture;

        @Override public void run() {
            final int c = ++runCount;
            if (c >= 5) {
                scheduledFuture.cancel(true);
                log.info("MyTimerRunnable cancel");
            }
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable {}  {}", c, ThreadContext.context().get("test"));
        }

    }

    private static class MyTimerRunnable2 implements Runnable {


        @Override public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable2  {}", ThreadContext.context().get("test"));
        }

    }

    private static class MyTimerRunnable2Queue implements Runnable, IExecutorQueue {

        ScheduledFuture<?> scheduledFuture;

        @Override public String getQueueName() {
            return "2";
        }

        @Override public void run() {
            scheduledFuture.cancel(true);
            log.info("MyTimerRunnable2Queue cancel {}", ThreadContext.context().get("test"));
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable2Queue {}", ThreadContext.context().get("test"));
        }

    }
}
