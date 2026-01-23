package executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
public class ExecutorQueue implements Executor, Runnable {

    protected transient final ReentrantLock reentrantLock = new ReentrantLock();
    private final Executor executor;
    private final String queueName;
    private final List<Thread> threads = Collections.synchronizedList(new ArrayList<>());
    private final ArrayBlockingQueue<Runnable> queue;
    private final QueuePolicyConst queuePolicy;
    private boolean addExecutor = false;

    public ExecutorQueue(Executor executor, String queueName, int maxQueueSize, QueuePolicyConst queuePolicy) {
        this.executor = executor;
        this.queueName = queueName;
        this.queue = new ArrayBlockingQueue<>(maxQueueSize);
        this.queuePolicy = queuePolicy;
    }


    @Override public void execute(Runnable command) {
        _execute(command);
    }

    private void _execute(Runnable command) {
        queuePolicy.execute(queue, command);
        reentrantLock.lock();
        try {
            if (queue.isEmpty()) return;
            if (!addExecutor) {
                addExecutor = true;
                executor.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    Runnable currentRunnable = null;

    @Override public void run() {
        try {
            currentRunnable = queue.poll();
            if (currentRunnable != null) {
                ExecutorVO executorVO = ExecutorVO.threadLocal();
                executorVO.setExecutorQueue(this);
                currentRunnable.run();
            }
        } catch (Throwable e) {
            log.error("ExecutorQueue error {} {}", currentRunnable.getClass(), currentRunnable, e);
        } finally {
            currentRunnable = null;
            reentrantLock.lock();
            try {
                if (!queue.isEmpty()) {
                    executor.execute(this);
                } else {
                    addExecutor = false;
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override public String toString() {
        return "ExecutorQueue{queueName='%s', currentRunnable=%s, %s}"
                .formatted(queueName, currentRunnable == null ? "null" : currentRunnable.getClass(), currentRunnable);
    }
}
