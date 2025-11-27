package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.reflect.AnnUtil;

import java.lang.annotation.Annotation;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 09:39
 **/
@Slf4j
@Getter
public class ExecutorJob implements Runnable {

    public static final ExecutorLog EXECUTOR_LOG = new ExecutorLog() {

        @Override public boolean off() {
            return false;
        }

        @Override public long logTime() {
            return 15;
        }

        @Override public long warningTime() {
            return 150;
        }

        @Override public Class<? extends Annotation> annotationType() {
            return ExecutorLog.class;
        }
    };

    /** 堆栈，也是任务名称，日志记录关键 */
    protected String stack;
    protected ThreadContext threadContext;
    protected ExecutorLog executorLog = EXECUTOR_LOG;
    private final Runnable runnable;

    public ExecutorJob(Runnable runnable) {
        this.runnable = runnable;
        this.stack = StackUtils.stack(0, 1);
        ExecutorLog executorLog1 = AnnUtil.ann(this.getClass(), ExecutorLog.class);
        if (executorLog1 != null) {
            this.executorLog = executorLog1;
        }
    }

    public ExecutorJob(Runnable runnable, ExecutorLog executorLog) {
        this.runnable = runnable;
        this.executorLog = executorLog == null ? EXECUTOR_LOG : executorLog;
        this.stack = StackUtils.stack(0, 1);
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            runnable.run();
        } catch (Throwable throwable) {
            log.error("{}", stack, throwable);
        } finally {
            this.threadContext = null;
            ExecutorMonitor.release();
            ThreadContext.cleanup();
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
