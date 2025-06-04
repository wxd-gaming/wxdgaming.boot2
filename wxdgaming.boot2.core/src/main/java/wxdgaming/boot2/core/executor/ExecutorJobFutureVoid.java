package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 09:39
 **/
@Slf4j
@Getter
class ExecutorJobFutureVoid extends ExecutorJob implements Runnable, IExecutorQueue {

    final IExecutorQueue iExecutorQueue;
    final CompletableFuture<Void> future = new CompletableFuture<>();

    public ExecutorJobFutureVoid(Runnable runnable) {
        super(runnable);
        iExecutorQueue = getRunnable() instanceof IExecutorQueue ? ((IExecutorQueue) getRunnable()) : null;
    }

    @Override public String queueName() {
        return iExecutorQueue == null ? null : iExecutorQueue.queueName();
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            getRunnable().run();
            future.complete(null);
        } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
        } finally {
            this.threadContext = null;
            ThreadContext.cleanup();
            ExecutorMonitor.release();
            runAfter();
        }
    }
}
