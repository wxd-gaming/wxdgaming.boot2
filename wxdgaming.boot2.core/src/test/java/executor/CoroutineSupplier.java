package executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 回调包装器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 13:17
 **/
@Slf4j
class CoroutineSupplier<R> implements Runnable, RunnableQueue {

    final ExecutorVO executorVO;
    final Supplier<R> runnable;
    final CompletableFuture<R> completableFuture = new CompletableFuture<>();

    public CoroutineSupplier(ExecutorVO executorVO, Supplier<R> runnable) {
        this.executorVO = executorVO;
        this.runnable = runnable;
    }

    @Override public String queueName() {
        if (runnable instanceof RunnableQueue runnableQueue) {
            return runnableQueue.queueName();
        }
        return null;
    }

    @Override public void run() {
        try {
            R r = runnable.get();
            executorVO.execute(() -> completableFuture.complete(r));
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            executorVO.execute(() -> completableFuture.completeExceptionally(e));
        }
    }

}
