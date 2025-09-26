package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 11:41
 **/
@Slf4j
public class ExecutorServiceVirtual extends ExecutorService {

    private final ArrayBlockingQueue<ExecutorJobVirtual> queue;
    private final QueuePolicy queuePolicy;
    private final ConcurrentMap<String, ExecutorQueue> queueMap = new ConcurrentHashMap<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicInteger threadSize = new AtomicInteger();
    private final Thread.Builder.OfVirtual ofVirtual;
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final String namePrefix;
    private final int core;
    /** 队列上限 */
    private final int queueSize;
    private final int warnSize;

    /** 如果队列已经达到上限默认是拒绝添加任务的 */
    ExecutorServiceVirtual(String namePrefix, int core, int queueSize, int warnSize, QueuePolicy queuePolicy) {
        this.ofVirtual = Thread.ofVirtual().name("Virtual-" + namePrefix + "-", 0);
        this.namePrefix = namePrefix;
        this.core = core;
        this.queueSize = queueSize;
        this.queuePolicy = queuePolicy;
        this.queue = new ArrayBlockingQueue<>(queueSize);
        this.warnSize = warnSize;
    }

    @Override public void stop() {
        stop.set(true);
    }

    @Override public void execute(Runnable command) {
        if (stop.get()) {
            throw new RejectedExecutionException("ExecutorServiceVirtual is stop");
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
                if (StringUtils.isNotBlank(iExecutorQueue.getQueueName())) {
                    queueMap
                            .computeIfAbsent(iExecutorQueue.getQueueName(), k -> new ExecutorQueue(k, this, this.queueSize, warnSize, this.queuePolicy))
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
            if (stop.get()) return;
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
                int size = queue.size();
                if (size > warnSize) {
                    log.warn("ExecutorService {} queueSize:{}, {}", ExecutorServiceVirtual.this.namePrefix, warnSize, executorJobVirtual.getRunnable());
                }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

}
