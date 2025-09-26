package executortest;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorServiceVirtual;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class ExecutorServiceVirtualTest {

    public static void main(String[] args) {

        ExecutorServiceVirtual executorService = ExecutorFactory.createVirtual("4", 1, 5, 1, QueuePolicyConst.AbortPolicy);
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnable());
        executorService.execute(new MyRunnableQueue());
        executorService.execute(new MyRunnableQueue());
        executorService.execute(new MyTimerRunnable());
        executorService.execute(new MyTimerRunnable());
        executorService.execute(new MyTimerRunnable2());
        executorService.execute(new MyTimerRunnable2());
        executorService.execute(new MyTimerRunnable2Queue());
        executorService.execute(new MyTimerRunnable2Queue());

        executorService.scheduleAtFixedRate(new MyTimerRunnable2Queue(), 1, 1, TimeUnit.SECONDS);

        // LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(15));
    }

    private static class MyRunnable implements Runnable {

        @Override public void run() {
            log.info("1");
        }
    }

    private static class MyRunnableQueue implements Runnable, IExecutorQueue {

        @Override public String getQueueName() {
            return "1";
        }

        @Override public void run() {
            log.info("1");
        }

    }

    private static class MyTimerRunnable implements Runnable {

        @Override public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable");
        }

    }

    private static class MyTimerRunnable2 implements Runnable {

        @Override public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable2");
        }

    }

    private static class MyTimerRunnable2Queue implements Runnable, IExecutorQueue {

        @Override public String getQueueName() {
            return "2";
        }

        @Override public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            log.info("MyTimerRunnable2Queue");
        }

    }
}
