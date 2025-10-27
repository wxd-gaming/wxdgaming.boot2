package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 09:39
 **/
@Slf4j
@Getter
public class ExecutorJob implements Runnable {

    /** 堆栈，也是任务名称，日志记录关键 */
    protected String stack;
    protected ThreadContext threadContext;
    private final Runnable runnable;

    public ExecutorJob(Runnable runnable) {
        this.runnable = runnable;
        this.stack = StackUtils.stack(0, 1);
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            ThreadStopWatch.nullInit(getStack());
            try {
                runnable.run();
            } finally {
                ThreadStopWatch.releasePrint();
            }
        } catch (Throwable throwable) {
            log.error("{}", stack, throwable);
        } finally {
            this.threadContext = null;
            ThreadContext.cleanup();
            ExecutorMonitor.release();
            runAfter();
        }
    }

    /** 堆栈，也是任务名称，日志记录关键 */
    public String getStack() {
        if (getRunnable() instanceof ExecutorJob executorJob) {
            return executorJob.getStack();
        }
        return stack;
    }

    /** 当等于true时不需要记录任务的执行耗时统计 */
    public boolean isIgnoreRunTimeRecord() {
        return false;
    }

    protected void runAfter() {
    }

}
