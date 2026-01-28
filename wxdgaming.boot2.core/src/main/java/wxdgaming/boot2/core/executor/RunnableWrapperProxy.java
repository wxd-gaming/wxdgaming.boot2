package wxdgaming.boot2.core.executor;

/**
 * 包装
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-28 10:37
 **/
interface RunnableWrapperProxy extends RunnableWarnTime, RunnableQueue {

    Object getRunnable();

    default boolean offWarnLog() {
        if (getRunnable() instanceof RunnableWarnTime runnableWarnTime) {
            return runnableWarnTime.offWarnLog();
        }
        return RunnableWarnTime.super.offWarnLog();
    }

    default long getExecutorWarnTime() {
        if (getRunnable() instanceof RunnableWarnTime runnableWarnTime) {
            return runnableWarnTime.getExecutorWarnTime();
        }
        return RunnableWarnTime.super.getExecutorWarnTime();
    }

    default long getSubmitWarnTime() {
        if (getRunnable() instanceof RunnableWarnTime runnableWarnTime) {
            return runnableWarnTime.getSubmitWarnTime();
        }
        return RunnableWarnTime.super.getSubmitWarnTime();
    }

    default String getQueueName() {
        if (getRunnable() instanceof RunnableQueue runnableQueue) {
            return runnableQueue.getQueueName();
        }
        return RunnableQueue.super.getQueueName();
    }

}
