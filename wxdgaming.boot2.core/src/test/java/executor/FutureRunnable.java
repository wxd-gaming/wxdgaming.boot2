package executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 回调包装器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 13:17
 **/
@Slf4j
class FutureRunnable implements Runnable, RunnableQueue {

    final ExecutorContext.Content content;
    final Runnable runnable;
    final CompletableFuture<Void> completableFuture = new CompletableFuture<>();

    public FutureRunnable(ExecutorContext.Content content, Runnable runnable) {
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
            runnable.run();
            completableFuture.complete(null);
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            completableFuture.completeExceptionally(e);
        }
    }

}
