package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行器队列
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 10:09
 **/
@Slf4j
@Getter
public class ExecutorQueue extends ExecutorJob {

    private final String queueName;
    private final int warnSize;
    private final Executor executor;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ArrayBlockingQueue<ExecutorJob> queue;
    private final QueuePolicy queuePolicy;

    public ExecutorQueue(String queueName, Executor executor, int queueSize, int warnSize, QueuePolicy queuePolicy) {
        super(null);
        this.queueName = queueName;
        this.warnSize = warnSize;
        this.executor = executor;
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.queuePolicy = queuePolicy;
    }

    private ExecutorJob convert(Runnable command) {
        ExecutorJob executorJob;
        if (!(command instanceof ExecutorJob)) {
            executorJob = new ExecutorJob(command);
            executorJob.stack = StackUtils.stack(0,2);
        } else {
            executorJob = (ExecutorJob) command;
        }
        if (!(command instanceof ExecutorJobScheduled.ScheduledExecutorJob) && executorJob.threadContext == null) {
            /*TODO 任务添加线程上下文*/
            executorJob.threadContext = new ThreadContext(ThreadContext.context());
        }
        return executorJob;
    }

    /**
     * 如果队列已满会直接拒绝抛出异常
     *
     * @throws IllegalStateException if this queue is full
     */
    public void execute(Runnable command) {
        queuePolicy.execute(queue, convert(command));
        if (queue.size() > warnSize) {
            log.warn("{} queue size: {}, {}", queueName, queue.size(), command);
        }
        checkExecute(false);
    }

    public void checkExecute(boolean force) {
        reentrantLock.lock();
        try {
            if (running.get()) {
                if (queue.isEmpty()) {
                    running.set(false);
                    return;
                }
                if (!force) {
                    return;
                }
            }
            running.set(true);
            executor.execute(this);
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override public void run() {
        String stack = "<Unknown>";
        try {
            ExecutorJob task = queue.poll();
            if (task != null) {
                stack = task.getStack();
                ExecutorMonitor.put(task);
                ThreadVO threadVO = ThreadContext.context().threadVO();
                threadVO.setExecutor(this.getExecutor());
                threadVO.setExecutorQueue(this);
                threadVO.setQueueName(this.getQueueName());
                task.run();
            }
        } catch (Throwable throwable) {
            log.error("{}", stack, throwable);
        } finally {
            ExecutorMonitor.release();
            ThreadContext.cleanup();
            checkExecute(true);
        }
    }

}
