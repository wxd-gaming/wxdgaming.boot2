package wxdgaming.boot2.core.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 虚拟线程执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 11:41
 **/
public class ExecutorServiceVirtual extends ExecutorService {

    private final ArrayBlockingQueue<ExecutorJobVirtual> queue;
    private final QueuePolicy queuePolicy;
    private final ConcurrentMap<String, ExecutorQueue> queueMap = new ConcurrentHashMap<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicInteger threadSize = new AtomicInteger();
    private final Thread.Builder.OfVirtual ofVirtual;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final int core;
    /** 队列上限 */
    private final int queueSize;

    /** 如果队列已经达到上限默认是拒绝添加任务的 */
    ExecutorServiceVirtual(String namePrefix, int core, int queueSize, QueuePolicy queuePolicy) {
        this.ofVirtual = Thread.ofVirtual().name("Virtual-" + namePrefix + "-", 0);
        this.core = core;
        this.queueSize = queueSize;
        this.queuePolicy = queuePolicy;
        this.queue = new ArrayBlockingQueue<>(queueSize);
    }

    @Override public void shutdown() {
        shutdown.set(true);
    }

    @Override public void execute(Runnable command) {
        if (shutdown.get()) {
            throw new RejectedExecutionException("ExecutorServiceVirtual is shutdown");
        }
        ExecutorJob executorJob;
        if (!(command instanceof ExecutorQueue)) {
            if (!(command instanceof ExecutorJob)) {
                executorJob = new ExecutorJob(command);
            } else {
                executorJob = (ExecutorJob) command;
            }

            if (!(command instanceof ExecutorJobScheduled.ScheduledExecutorJob) && executorJob.threadContext == null) {
                /*TODO 任务添加线程上下文*/
                executorJob.threadContext = new ThreadContext(ThreadContext.context());
            }

            if (executorJob instanceof IExecutorQueue iExecutorQueue) {
                if (Utils.isNotBlank(iExecutorQueue.queueName())) {
                    queueMap
                            .computeIfAbsent(iExecutorQueue.queueName(), k -> new ExecutorQueue(k, this, this.queueSize, this.queuePolicy))
                            .execute(executorJob);
                    return;
                }
            }
        } else {
            executorJob = (ExecutorJob) command;
        }
        ExecutorJobVirtual executorJobVirtual = new ExecutorJobVirtual(executorJob);
        checkExecute(executorJobVirtual);
    }

    private class ExecutorJobVirtual extends ExecutorJob {

        public ExecutorJobVirtual(Runnable runnable) {
            super(runnable);
        }

        @Override protected void runAfter() {
            reentrantLock.lock();
            try {
                threadSize.decrementAndGet();
                checkExecute(null);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    private void checkExecute(ExecutorJobVirtual executorJobVirtual) {
        reentrantLock.lock();
        try {
            if (shutdown.get()) return;
            if (threadSize.get() < this.core) {
                Runnable task;
                if (executorJobVirtual != null) {
                    task = executorJobVirtual;
                } else {
                    task = queue.poll();
                }
                if (task != null) {
                    threadSize.incrementAndGet();
                    this.ofVirtual.start(task);
                }
            } else {
                this.queuePolicy.execute(queue, executorJobVirtual);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

}
