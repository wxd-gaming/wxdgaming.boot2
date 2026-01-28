package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.concurrent.TimeUnit;

/** 当前线程上下文 */
@Getter
@Setter
public class ExecutorMonitorContext {

    private Thread thread;
    private AbstractExecutorService executorService;
    private ExecutorQueue executorQueue;
    private Runnable runnable;
    private final long startTime;
    private final long actualStartTime; // 用于记录实际开始时间
    private ExecutorMonitorContextStopWatch stopWatch = null;


    public ExecutorMonitorContext() {
        this.startTime = System.nanoTime();
        this.actualStartTime = MyClock.millis();
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.stopWatch = new ExecutorMonitorContextStopWatch(TimeUnit.MICROSECONDS, String.valueOf(this.runnable));
    }

    public void startWatch(Object name) {
        this.stopWatch.start(String.valueOf(name));
    }

    public void stopWatch() {
        this.stopWatch.stop();
    }

    /** 纳秒 */
    public long cost() {
        return System.nanoTime() - startTime;
    }

    /** 毫秒 */
    public long costMillis() {
        return TimeUnit.NANOSECONDS.toMillis(cost());
    }

    @Override public String toString() {
        return """
                    线程: %s
                  线程池: %s
                    队列: %s
                    任务: %s
                开始时间: %s
                执行耗时: %s
                耗时追踪: ↓↓↓↓
                %s
                """.formatted(
                thread.getName(),
                String.valueOf(executorService),
                String.valueOf(executorQueue),
                String.valueOf(runnable),
                MyClock.formatDate(this.actualStartTime),
                costMillis() + " ms",
                stopWatch.toString()
        );
    }

}
