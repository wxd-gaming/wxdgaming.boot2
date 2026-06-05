package executor4;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.executor.StackUtils;

/**
 * 执行器包装
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 20:51
 **/
@Slf4j
public class RunnableWrapper implements Runnable {

    public static RunnableWrapper wrapper(Runnable runnable) {
        if (runnable instanceof RunnableWrapper runnableWrapper) {
            return runnableWrapper;
        }
        return new RunnableWrapper(runnable);
    }

    private final Runnable task;
    private String stack;

    public RunnableWrapper(Runnable task) {
        this(task, true);
    }

    public RunnableWrapper(Runnable task, boolean initStack) {
        this.task = task;
        if (initStack) {
            this.initStackTrace(1, 1);
        }
    }

    public void initStackTrace(int initSkip, int skip) {
        stack = StackUtils.stack(initSkip, skip);
    }

    @Override
    public void run() {
        Throwable throwable = null;
        try {
            this.task.run();
        } catch (Throwable t) {
            throwable = t;
            log.error("执行异常:{}", this.toString(), t);
        } finally {
            afterExecute(task, throwable);
        }
    }

    public void afterExecute(Runnable task, Throwable throwable) {

    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(stack)) {
            return String.valueOf(task);
        }
        return stack + "->" + String.valueOf(task);
    }


}
