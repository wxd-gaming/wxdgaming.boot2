package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.timer.MyClock;

/**
 * 执行器包装
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-26 11:30
 **/
@Getter
@Setter
class RunnableWrapper implements Runnable {

    protected final long newTime;
    protected final long actualNewTime;
    protected Runnable runnable;
    protected ExecutorContext.Content executorContent = new ExecutorContext.Content();

    public RunnableWrapper() {
        this.newTime = System.nanoTime();
        this.actualNewTime = MyClock.millis();
    }


    @Override public void run() {
        this.runnable.run();
    }

    @Override public String toString() {
        return String.valueOf(runnable);
    }
}
