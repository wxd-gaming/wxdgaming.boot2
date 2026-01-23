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
class CoroutineRunnable implements Runnable, RunnableQueue {

    final ExecutorVO executorVO;
    final Runnable runnable;
    final CompletableFuture<Void> completableFuture = new CompletableFuture<>();

    public CoroutineRunnable(ExecutorVO executorVO, Runnable runnable) {
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
            runnable.run();
            executorVO.execute(() -> completableFuture.complete(null));
        } catch (Throwable e) {
            log.error("{} {} error", runnable.getClass(), runnable.toString(), e);
            executorVO.execute(() -> completableFuture.completeExceptionally(e));
        }
    }

}
