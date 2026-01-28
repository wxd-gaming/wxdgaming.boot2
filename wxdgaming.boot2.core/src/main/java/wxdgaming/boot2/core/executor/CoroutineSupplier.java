package wxdgaming.boot2.core.executor;

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
class CoroutineSupplier<R> implements Runnable, RunnableWrapperProxy {

    final ExecutorContext.Content content;
    final Supplier<R> runnable;
    final CompletableFuture<R> completableFuture = new CompletableFuture<>();

    public CoroutineSupplier(ExecutorContext.Content content, Supplier<R> runnable) {
        this.content = content;
        this.runnable = runnable;
    }

    @Override public Supplier<R> getRunnable() {
        return runnable;
    }

    @Override public void run() {
        try {
            ExecutorContext.context().getData().putAll(content.getData());
            R r = runnable.get();
            content.execute(() -> completableFuture.complete(r));
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            content.execute(() -> completableFuture.completeExceptionally(e));
        }
    }

    @Override public String toString() {
        return String.valueOf(runnable);
    }
}
