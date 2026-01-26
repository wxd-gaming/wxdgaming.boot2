package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行器队列
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 15:07
 **/
@Slf4j
@Getter
public class ExecutorQueue extends RunnableWrapper implements Executor, Runnable {

    protected transient final ReentrantLock reentrantLock = new ReentrantLock();
    private final AbstractExecutorService abstractExecutorService;
    private final String queueName;
    private final List<Thread> threads = Collections.synchronizedList(new ArrayList<>());
    private final ArrayBlockingQueue<RunnableWrapper> queue;
    private final int maxQueueSize;
    private final QueuePolicyConst queuePolicy;
    private boolean addExecutor = false;

    public ExecutorQueue(AbstractExecutorService abstractExecutorService, String queueName, int maxQueueSize, QueuePolicyConst queuePolicy) {
        this.abstractExecutorService = abstractExecutorService;
        this.queueName = queueName;
        this.queue = new ArrayBlockingQueue<>(maxQueueSize);
        this.maxQueueSize = maxQueueSize;
        this.queuePolicy = queuePolicy;
    }


    @Override public void execute(@NonNull Runnable command) {
        _execute(command);
    }

    private void _execute(Runnable command) {
        RunnableWrapper runnableWrapper = new RunnableWrapper();
        runnableWrapper.setRunnable(command);
        ExecutorContext.Content context = ExecutorContext.context();
        runnableWrapper.getExecutorContent().getData().putAll(context.getData());
        queuePolicy.execute(queue, runnableWrapper);
        reentrantLock.lock();
        try {
            if (queue.isEmpty()) return;
            if (!addExecutor) {
                addExecutor = true;
                abstractExecutorService.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override public void run() {
        try {
            RunnableWrapper runnableWrapper = queue.poll();
            this.runnable = runnableWrapper;
            if (this.runnable != null) {
                ExecutorContext.Content content = ExecutorContext.context();
                content.newTime = runnableWrapper.newTime;
                content.executorQueue = this;
                content.runnable = this.runnable;
                content.getData().putAll(runnableWrapper.getExecutorContent().getData());
                this.runnable.run();
            }
        } catch (Throwable e) {
            log.error("ExecutorQueue error {}", this.runnable, e);
        } finally {
            this.runnable = null;
            reentrantLock.lock();
            try {
                if (!queue.isEmpty()) {
                    abstractExecutorService.execute(this);
                } else {
                    addExecutor = false;
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override public String toString() {
        return "ExecutorQueue{queueName='%s', maxQueueSize=%d}".formatted(queueName, maxQueueSize);
    }
}
