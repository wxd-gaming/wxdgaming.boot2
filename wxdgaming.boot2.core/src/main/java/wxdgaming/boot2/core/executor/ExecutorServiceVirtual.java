package wxdgaming.boot2.core.executor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 虚拟线程执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 11:41
 **/
public class ExecutorServiceVirtual extends ExecutorService {

    protected final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    protected final ConcurrentMap<String, ExecutorQueue> queueMap = new ConcurrentHashMap<>();
    protected final ReentrantLock reentrantLock = new ReentrantLock();
    protected final AtomicInteger threadSize = new AtomicInteger();
    protected final Thread.Builder.OfVirtual ofVirtual;
    protected final int core;

    ExecutorServiceVirtual(String namePrefix, int core) {
        this.ofVirtual = Thread.ofVirtual().name("Virtual-" + namePrefix + "-", 0);
        this.core = core;
    }

    @Override public void execute(Runnable command) {
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
                            .computeIfAbsent(iExecutorQueue.queueName(), k -> new ExecutorQueue(this))
                            .execute(executorJob);
                    return;
                }
            }
        } else {
            executorJob = (ExecutorJob) command;
        }
        ExecutorJobVirtual executorJobVirtual = new ExecutorJobVirtual(executorJob);
        this.queue.add(executorJobVirtual);
        checkExecute();
    }

    private class ExecutorJobVirtual extends ExecutorJob {

        public ExecutorJobVirtual(Runnable runnable) {
            super(runnable);
        }

        @Override protected void runAfter() {
            reentrantLock.lock();
            try {
                threadSize.decrementAndGet();
                checkExecute();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    protected void checkExecute() {
        reentrantLock.lock();
        try {
            if (threadSize.get() < this.core) {
                Runnable task = queue.poll();
                if (task != null) {
                    threadSize.incrementAndGet();
                    this.ofVirtual.start(task);
                }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

}
