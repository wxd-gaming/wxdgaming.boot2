package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 虚拟线程池
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 21:04
 **/
@Slf4j
public class ExecutorServiceVirtual extends AbstractExecutorService {

    final Thread.Builder.OfVirtual ofVirtual;
    final AtomicInteger threadNum = new AtomicInteger(0);
    final ReentrantLock lock = new ReentrantLock();

    public ExecutorServiceVirtual(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        super(namePrefix, threadSize, queueSize, queuePolicy);
        ofVirtual = Thread.ofVirtual().name(namePrefix + "-", 0);
    }

    @Override protected void checkExecute() {
        lock.lock();
        try {
            if (getQueue().isEmpty()) {
                return;
            }
            if (threadNum.get() < getThreadSize()) {
                newThread();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override protected void newThread() {
        RunnableWrapper task = getQueue().poll();
        if (task == null) return;
        threadNum.incrementAndGet();
        ofVirtual.start(() -> {
            setExecutorContext(task);
            try {
                task.run();
            } catch (Throwable e) {
                log.error("{} {} error", task.getClass(), task, e);
            } finally {
                ExecutorContext.cleanup();
                threadNum.decrementAndGet();
                checkExecute();
            }
        });
    }

}
