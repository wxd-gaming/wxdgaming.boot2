package wxdgaming.boot2.core.executor;

import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 执行器批量处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-27 09:15
 **/
public class ExecutorLatch {

    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final Executor executor;

    public ExecutorLatch() {
        executor = null;
    }

    public ExecutorLatch(Executor executor) {
        this.executor = executor;
    }

    public void executor(Runnable runnable) {
        AssertUtil.isNull(this.executor, "executor is null");
        executor(runnable, executor);
    }

    public void executor(Runnable runnable, Executor targetExecutor) {
        atomicInteger.incrementAndGet();
        AbstractEventRunnable executorJob = new AbstractEventRunnable() {
            @Override public void onEvent() throws Exception {
                try {
                    runnable.run();
                } finally {
                    atomicInteger.decrementAndGet();
                }
            }
        };
        targetExecutor.execute(executorJob);
    }


    public void executorAwait(Runnable runnable) throws InterruptedException {
        AssertUtil.isNull(this.executor, "executor is null");
        executorAwait(runnable, executor);
    }

    public void executorAwait(Runnable runnable, Executor targetExecutor) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AbstractEventRunnable executorJob = new AbstractEventRunnable() {
            @Override public void onEvent() throws Exception {
                try {
                    runnable.run();
                } finally {
                    atomicInteger.decrementAndGet();
                }
            }
        };
        targetExecutor.execute(executorJob);
        countDownLatch.await();
    }

    public void await() throws InterruptedException {
        while (atomicInteger.get() > 0) {
            LockSupport.parkNanos(1);
        }
    }

}
