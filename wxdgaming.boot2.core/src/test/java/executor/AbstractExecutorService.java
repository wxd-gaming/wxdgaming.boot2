package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 线程池
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 20:57
 */
@Slf4j
@Getter
public abstract class AbstractExecutorService implements Executor {

    private final String namePrefix;
    private final int threadSize;
    private final int queueSize;
    private final QueuePolicyConst queuePolicy;
    private final ArrayBlockingQueue<Runnable> queue;
    private final ConcurrentHashMap<String, ExecutorQueue> executorQueues = new ConcurrentHashMap<>();
    private final AtomicBoolean stoping = new AtomicBoolean(false);

    public AbstractExecutorService(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        this.namePrefix = namePrefix;
        this.threadSize = threadSize;
        this.queueSize = queueSize;
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.queuePolicy = queuePolicy;
    }

    /**
     * 延迟执行一次的任务
     *
     * @param runnable 需要执行
     * @param delay    延迟时间
     * @param unit     时间单位
     * @return 返回任务挂载，可以执行取消
     */
    public CancelHolding schedule(Runnable runnable, long delay, TimeUnit unit) {
        ScheduledRunnable schedule = ScheduledRunnable.schedule(this, runnable, delay, unit);
        return schedule.cancelHolding;
    }

    /**
     * 定时执行任务，无论上一次是否执行完成都会执行第二次
     *
     * @param runnable     需要执行的任务
     * @param initialDelay 延迟时间
     * @param delay        间隔时间
     * @param unit         时间单位
     * @return 返回任务挂载，可以执行取消
     */
    public CancelHolding scheduleAtFixedRate(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        ScheduledRunnable scheduledRunnable = ScheduledRunnable.scheduleAtFixedRate(this, runnable, initialDelay, delay, unit);
        return scheduledRunnable.cancelHolding;
    }

    /**
     * 定时执行任务，如果本次执行卡组了尚未完成，不会执行下一次
     *
     * @param runnable     需要执行的任务
     * @param initialDelay 延迟时间
     * @param delay        间隔时间
     * @param unit         时间单位
     * @return 返回任务挂载，可以执行取消
     */
    public CancelHolding scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        ScheduledRunnable scheduledRunnable = ScheduledRunnable.scheduleWithFixedDelay(this, runnable, initialDelay, delay, unit);
        return scheduledRunnable.cancelHolding;
    }

    /** 异步 */
    public CompletableFuture<Void> future(Runnable runnable) {
        FutureRunnable futureRunnable = new FutureRunnable(runnable);
        execute(futureRunnable);
        return futureRunnable.completableFuture;
    }

    /** 异步 */
    public <R> CompletableFuture<R> future(Supplier<R> supplier) {
        FutureSupplier<R> futureSupplier = new FutureSupplier<>(supplier);
        execute(futureSupplier);
        return futureSupplier.completableFuture;
    }

    /** 异步 协程(coroutine) 会回到当前调用线程 */
    public CompletableFuture<Void> coroutine(Runnable runnable) {
        ExecutorVO executorVO = ExecutorVO.threadLocal();
        CoroutineRunnable coroutineRunnable = new CoroutineRunnable(executorVO, runnable);
        execute(coroutineRunnable);
        return coroutineRunnable.completableFuture;
    }

    /** 异步 协程(coroutine) 会回到当前调用线程 */
    public <R> CompletableFuture<R> coroutine(Supplier<R> supplier) {
        ExecutorVO executorVO = ExecutorVO.threadLocal();
        CoroutineSupplier<R> coroutineSupplier = new CoroutineSupplier<>(executorVO, supplier);
        execute(coroutineSupplier);
        return coroutineSupplier.completableFuture;
    }

    @Override public void execute(@NonNull Runnable command) {
        if (command instanceof RunnableQueue runnableQueue) {
            String queueName = runnableQueue.queueName();
            if (StringUtils.isNotBlank(queueName)) {
                ExecutorQueue executorQueue = executorQueues.computeIfAbsent(
                        queueName,
                        l -> new ExecutorQueue(this, queueName, queueSize, queuePolicy)
                );
                executorQueue.execute(command);
                return;
            }
        }
        _execute(command);
    }

    protected void _execute(Runnable command) {
        queuePolicy.execute(queue, command);
        checkExecute();
    }

    protected abstract void checkExecute();

    protected abstract void newThread();

    @Override public String toString() {
        return "%s{namePrefix='%s', threadSize=%d, queueSize=%d, queuePolicy=%s, stoping=%s}"
                .formatted(this.getClass().getSimpleName(), namePrefix, threadSize, queueSize, queuePolicy, stoping);
    }

}
