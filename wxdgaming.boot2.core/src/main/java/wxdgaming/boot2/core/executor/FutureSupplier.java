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
class FutureSupplier<R> implements Runnable, RunnableWrapperProxy {

    final ExecutorContext.ExecutorDTO executorDTO;
    final Supplier<R> runnable;
    final CompletableFuture<R> completableFuture = new CompletableFuture<>();

    public FutureSupplier(ExecutorContext.ExecutorDTO executorDTO, Supplier<R> runnable) {
        this.executorDTO = executorDTO;
        this.runnable = runnable;
    }

    @Override public Object getRunnable() {
        return runnable;
    }

    @Override public void run() {
        try {
            ExecutorContext.context().getData().putAll(executorDTO.getData());
            completableFuture.complete(runnable.get());
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            completableFuture.completeExceptionally(e);
        }
    }

    @Override public String toString() {
        return String.valueOf(runnable);
    }
}
