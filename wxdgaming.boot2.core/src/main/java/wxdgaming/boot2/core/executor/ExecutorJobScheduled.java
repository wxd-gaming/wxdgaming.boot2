package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 09:36
 **/
@Slf4j
@Getter
class ExecutorJobScheduled implements Runnable {

    private final Executor executor;
    private final ScheduledExecutorJob targetRunnable;
    /** 如果true 如果当前正在运行不执行下一次 */
    private final boolean atFixedRate;
    private final AtomicBoolean running = new AtomicBoolean(false);
    @Setter private ScheduledFuture<?> scheduledFuture;

    /**
     * 定时器任务
     *
     * @param executor    执行器
     * @param atFixedRate 如果true 如果当前正在运行不执行下一次
     */
    public ExecutorJobScheduled(Executor executor, Runnable targetRunnable, boolean atFixedRate) {
        this.executor = executor;
        this.targetRunnable = new ScheduledExecutorJob(targetRunnable);
        this.atFixedRate = atFixedRate;
    }

    /** 对任务进行一次包装 */
    public class ScheduledExecutorJob extends ExecutorJob implements IExecutorQueue {

        final IExecutorQueue iExecutorQueue;

        public ScheduledExecutorJob(Runnable runnable) {
            super(runnable);
            iExecutorQueue = getRunnable() instanceof IExecutorQueue ? ((IExecutorQueue) getRunnable()) : null;
        }

        @Override protected ThreadContext getThreadContext() {
            return null;
        }

        @Override public String queueName() {
            return iExecutorQueue == null ? null : iExecutorQueue.queueName();
        }

        /** 标记本次运行结束 */
        @Override protected void runAfter() {
            ExecutorJobScheduled.this.running.set(false);
        }
    }

    @Override public void run() {
        try {
            if (atFixedRate) {
                if (!running.compareAndSet(false, true)) {
                    /*TODO 如果当前正在运行不执行下一次*/
                    return;
                }
            }
            executor.execute(targetRunnable);
        } catch (Throwable throwable) {
            log.error("", throwable);
        }
    }
}
