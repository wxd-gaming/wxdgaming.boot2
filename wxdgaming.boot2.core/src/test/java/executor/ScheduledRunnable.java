package executor;

import wxdgaming.boot2.core.collection.concurrent.ConcurrentHashSet;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 定时器逻辑
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 17:39
 **/
class ScheduledRunnable implements Runnable, Comparable<ScheduledRunnable> {

    private static final ConcurrentHashSet<ScheduledRunnable> scheduledRunnableSet = new ConcurrentHashSet<>();

    static {
        Thread thread = new Thread(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(500));
                long now = MyClock.millis();
                ArrayList<ScheduledRunnable> scheduledRunnables = new ArrayList<>(scheduledRunnableSet);
                for (ScheduledRunnable scheduledRunnable : scheduledRunnables) {
                    if (scheduledRunnable.cancelHolding.isCancel()) {
                        /* TODO 如果任务已经取消本次不再执行，并且不再继续添加 */
                        scheduledRunnableSet.remove(scheduledRunnable);
                        continue;
                    }
                    if (scheduledRunnable.nextRunTime > now) {
                        /* TODO 因为有序的，如果当前任务都还没有到达指定运行周期，那么后面的也不需要检查了 */
                        break;
                    }
                    /*时间到了可以运行*/
                    scheduledRunnableSet.remove(scheduledRunnable);
                    scheduledRunnable.run();
                    if (scheduledRunnable.cancelHolding.isCancel())
                        /* TODO 已经取消*/
                        continue;
                    if (scheduledRunnable.withFixedDelay)
                        /* TODO 需要等到当前执行完成 */
                        continue;
                    if (scheduledRunnable.delay < 1) {
                        /*TODO 表示单纯任务*/
                        continue;
                    }
                    /*表示需要继续运行*/
                    scheduledRunnable.resetNextRunTime();
                    scheduledRunnableSet.add(scheduledRunnable);
                }
            }
        });
        thread.setName("ScheduledRunnable");
        thread.setDaemon(true);
        thread.start();
    }

    public static ScheduledRunnable schedule(Executor executor, Runnable runnable, long delay, TimeUnit unit) {
        return new ScheduledRunnable(executor, runnable, false, delay, 0, unit);
    }

    /**
     * 定时执行任务，无论上一次是否执行完成都会执行第二次
     *
     * @param runnable     需要执行的任务
     * @param initialDelay 延迟时间
     * @param delay        间隔时间
     * @param unit         时间单位
     */
    public static ScheduledRunnable scheduleAtFixedRate(Executor executor, Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return new ScheduledRunnable(executor, runnable, false, initialDelay, delay, unit);
    }

    /**
     * 定时执行任务，如果本次执行卡组了尚未完成，不会执行下一次
     *
     * @param runnable     需要执行的任务
     * @param initialDelay 延迟时间
     * @param delay        间隔时间
     * @param unit         时间单位
     */
    public static ScheduledRunnable scheduleWithFixedDelay(Executor executor, Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return new ScheduledRunnable(executor, runnable, true, initialDelay, delay, unit);
    }

    protected final Executor executor;
    protected final RunnableProxy runnableProxy;
    /** true 如果本次执行卡组了尚未完成，不会执行下一次 */
    protected final boolean withFixedDelay;
    protected final long delay;
    protected final TimeUnit unit;
    protected long nextRunTime;
    protected final CancelHolding cancelHolding = new CancelHolding();

    private ScheduledRunnable(Executor executor, Runnable runnable, boolean withFixedDelay, long initialDelay, long delay, TimeUnit unit) {
        this.executor = executor;
        this.runnableProxy = new RunnableProxy(runnable);
        this.withFixedDelay = withFixedDelay;
        this.delay = delay;
        this.unit = unit;
        this.nextRunTime = MyClock.millis() + unit.toMillis(initialDelay);
        scheduledRunnableSet.add(this);
    }

    void resetNextRunTime() {
        this.nextRunTime = MyClock.millis() + unit.toMillis(delay);
    }

    @Override public void run() {
        executor.execute(this.runnableProxy);
    }

    @Override public int compareTo(ScheduledRunnable o) {
        if (this.nextRunTime > o.nextRunTime) {
            return 1;
        } else if (this.nextRunTime < o.nextRunTime) {
            return -1;
        }
        return Integer.compare(this.hashCode(), o.hashCode());
    }

    protected class RunnableProxy implements Runnable {

        private final Runnable runnable;

        public RunnableProxy(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override public void run() {
            try {
                this.runnable.run();
            } finally {
                if (!cancelHolding.isCancel()) {
                    if (ScheduledRunnable.this.withFixedDelay) {
                        /*表示需要继续运行*/
                        ScheduledRunnable.this.resetNextRunTime();
                        scheduledRunnableSet.add(ScheduledRunnable.this);
                    }
                }
            }
        }

        @Override public String toString() {
            return String.valueOf(runnable);
        }
    }

}
