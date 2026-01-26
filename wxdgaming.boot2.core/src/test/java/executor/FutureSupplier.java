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
class FutureSupplier<R> implements Runnable, RunnableQueue {

    final ExecutorContext.Content content;
    final Supplier<R> runnable;
    final CompletableFuture<R> completableFuture = new CompletableFuture<>();

    public FutureSupplier(ExecutorContext.Content content, Supplier<R> runnable) {
        this.content = content;
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
            ExecutorContext.context().getData().putAll(content.getData());
            completableFuture.complete(runnable.get());
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            completableFuture.completeExceptionally(e);
        }
    }

}
