package executor;

import wxdgaming.boot2.core.executor.StackUtils;

/**
 * 事件执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 14:06
 **/
public abstract class AbstractEventRunnable implements Runnable, RunnableQueue, RunnableWarnTime {

    private final String sourceLine;

    public AbstractEventRunnable() {
        this.sourceLine = StackUtils.stack(1, 1);
    }

    @Override public long getSubmitWarnTime() {
        return 33;
    }

    @Override public long getExecutorWarnTime() {
        return 33;
    }

    @Override public String queueName() {
        return null;
    }

    @Override public String toString() {
        return sourceLine;
    }

}
