package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行器队列
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 10:09
 **/
@Slf4j
public class ExecutorQueue extends ExecutorJob implements Executor {

    private final Executor executor;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final LinkedBlockingQueue<ExecutorJob> queue = new LinkedBlockingQueue<>();

    public ExecutorQueue(Executor executor) {
        super(null);
        this.executor = executor;
    }

    @Override public void execute(Runnable command) {
        if (!(command instanceof ExecutorJob)) {
            command = new ExecutorJob(command);
        }
        queue.add((ExecutorJob) command);
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
                task.run();
            }
        } catch (Throwable throwable) {
            log.error("{}", stack, throwable);
        } finally {
            ExecutorMonitor.release();
            checkExecute(true);
        }
    }

}
