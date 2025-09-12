package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.timer.CronExpress;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * cron 表达式时间触发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-09-27 10:40
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractCronTrigger extends ExecutorEvent implements Runnable, IExecutorQueue, Comparable<AbstractCronTrigger> {

    protected final CronExpress cronExpress;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final ReentrantLock lock = new ReentrantLock();
    protected final AtomicBoolean runEnd = new AtomicBoolean(true);
    protected long nextRunTime = -1;

    public AbstractCronTrigger(Method method, CronExpress cronExpress) {
        super(method);
        this.stack = StackUtils.stack();
        this.cronExpress = cronExpress;
    }

    public AbstractCronTrigger(String cron) {
        this.cronExpress = new CronExpress(cron, TimeUnit.SECONDS, 0);
    }

    public int index() {
        return Const.SORT_DEFAULT;
    }

    public boolean scheduleAtFixedRate() {
        return false;
    }

    public long getNextRunTime() {
        if (nextRunTime == -1) {
            this.nextRunTime = this.cronExpress.validateTimeAfterMillis();
        }
        return nextRunTime;
    }

    /** 检查时间是否满足 */
    public boolean checkRunTime(long millis) {
        return millis >= getNextRunTime();
    }

    public boolean isAsync() {
        return false;
    }

    @Override public void run() {
        try {
            super.run();
        } finally {
            lock.lock();
            try {
                /*标记为执行完成*/
                runEnd.set(true);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public int compareTo(AbstractCronTrigger o) {
        if (this.index() != o.index())
            return Integer.compare(this.index(), o.index());
        if (!Objects.equals(this.getClass().getName(), o.getClass().getName())) {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
        return Integer.compare(this.hashCode(), o.hashCode());
    }

}
