package executor;

import org.springframework.scheduling.support.CronExpression;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentHashSet;
import wxdgaming.boot2.core.timer.MyClock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * cron 表达式 定时器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 14:34
 **/
class CronRunnable implements Runnable {

    private static final ConcurrentHashSet<CronRunnable> CONCURRENT_HASH_SET = new ConcurrentHashSet<>();

    static {
        Thread thread = new Thread(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(200));
                long now = MyClock.millis();
                ArrayList<CronRunnable> runnables = new ArrayList<>(CONCURRENT_HASH_SET);
                for (CronRunnable cronRunnable : runnables) {
                    if (cronRunnable.cancelHolding.isCancel()) {
                        /* TODO 如果任务已经取消本次不再执行，并且不再继续添加 */
                        CONCURRENT_HASH_SET.remove(cronRunnable);
                        continue;
                    }
                    if (cronRunnable.nextRunTime > now) {
                        /* TODO 因为有序的，如果当前任务都还没有到达指定运行周期，那么后面的也不需要检查了 */
                        break;
                    }
                    /* TODO 先从任务池删除*/
                    CONCURRENT_HASH_SET.remove(cronRunnable);
                    cronRunnable.run();
                    /* TODO 重新计算下次需要运行的时间 */
                    cronRunnable.resetNextRunTime();
                    if (cronRunnable.cancelHolding.isCancel()) {
                        continue;
                    }
                    /* TODO 表示需要继续运行 */
                    CONCURRENT_HASH_SET.add(cronRunnable);
                }
            }
        });
        thread.setName("CronRunnable");
        thread.setDaemon(true);
        thread.start();
    }

    final Executor executor;
    final CronExpression cronExpression;
    final Runnable runnable;
    final CancelHolding cancelHolding = new CancelHolding();
    long nextRunTime;

    public CronRunnable(Executor executor, CronExpression cronExpression, Runnable runnable) {
        this.executor = executor;
        this.cronExpression = cronExpression;
        this.runnable = runnable;
        resetNextRunTime();
        CONCURRENT_HASH_SET.add(this);
    }

    void resetNextRunTime() {
        LocalDateTime localDateTime = cronExpression.next(LocalDateTime.now());
        if (localDateTime == null) {
            cancelHolding.cancel();
            return;
        }
        this.nextRunTime = MyClock.time2Milli(localDateTime);
    }

    @Override public void run() {
        executor.execute(runnable);
    }

}
