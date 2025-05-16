package wxdgaming.boot2.core.executor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 09:39
 **/
@Slf4j
class ExecutorJob implements Runnable {

    private final String stack;
    @Getter(AccessLevel.PROTECTED) private final Runnable runnable;

    public ExecutorJob(Runnable runnable) {
        this.runnable = runnable;
        this.stack = Utils.stack();
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            runnable.run();
        } catch (Throwable throwable) {
            log.error("{}", stack, throwable);
        } finally {
            runAfter();
            ExecutorMonitor.release();
        }
    }

    public String getStack() {
        if (getRunnable() instanceof ExecutorJob executorJob) {
            return executorJob.getStack();
        }
        return stack;
    }

    protected void runAfter() {
    }

}
