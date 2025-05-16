package wxdgaming.boot2.core.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface ExecutorService extends Executor {

    default CompletableFuture<Void> future(Runnable runnable) {
        ExecutorJobFutureVoid executorJobFuture = new ExecutorJobFutureVoid(runnable);
        execute(executorJobFuture);
        return executorJobFuture.getFuture();
    }

    default <T> CompletableFuture<T> future(Supplier<T> supplier) {
        ExecutorJobFuture<T> executorJobFuture = new ExecutorJobFuture<>(supplier);
        execute(executorJobFuture);
        return executorJobFuture.getFuture();
    }

    /** 延迟执行一次的任务 */
    default ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        ExecutorJobScheduled executorJobScheduled = new ExecutorJobScheduled(this, command, true);
        ScheduledFuture<?> scheduledFuture = ExecutorFactory.scheduledExecutorService.schedule(executorJobScheduled, delay, unit);
        executorJobScheduled.setSchedule(scheduledFuture);
        return scheduledFuture;
    }

    /** 上一次任务卡住了，不会触发下一次 */
    default ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ExecutorJobScheduled executorJobScheduled = new ExecutorJobScheduled(this, command, true);
        ScheduledFuture<?> scheduledFuture = ExecutorFactory.scheduledExecutorService.scheduleAtFixedRate(executorJobScheduled, initialDelay, period, unit);
        executorJobScheduled.setSchedule(scheduledFuture);
        return scheduledFuture;
    }

    /** 上一次任务卡住了，依然会触发下一次 */
    default ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ExecutorJobScheduled executorJobScheduled = new ExecutorJobScheduled(this, command, false);
        ScheduledFuture<?> scheduledFuture = ExecutorFactory.scheduledExecutorService.scheduleWithFixedDelay(executorJobScheduled, initialDelay, period, unit);
        executorJobScheduled.setSchedule(scheduledFuture);
        return scheduledFuture;
    }
}
