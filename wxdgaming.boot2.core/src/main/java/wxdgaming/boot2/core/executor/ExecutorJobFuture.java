package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 09:39
 **/
@Slf4j
@Getter
class ExecutorJobFuture<T> extends ExecutorJob implements Runnable, IExecutorQueue {

    final IExecutorQueue iExecutorQueue;
    final CompletableFuture<T> future = new CompletableFuture<>();
    final Supplier<T> supplier;

    public ExecutorJobFuture(Supplier<T> supplier) {
        super(null);
        this.supplier = supplier;
        iExecutorQueue = supplier instanceof IExecutorQueue eq ? eq : null;
    }

    @Override public String getQueueName() {
        return iExecutorQueue == null ? null : iExecutorQueue.getQueueName();
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        return true;
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            future.complete(this.supplier.get());
        } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
        } finally {
            this.threadContext = null;
            ExecutorMonitor.release();
            ThreadContext.cleanup();
            runAfter();
        }
    }
}
