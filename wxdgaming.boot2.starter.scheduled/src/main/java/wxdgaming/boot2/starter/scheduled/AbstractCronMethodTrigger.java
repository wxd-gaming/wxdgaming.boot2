package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.executor.AbstractMethodRunnable;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.core.executor.StackUtils;
import wxdgaming.boot2.core.locks.Monitor;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
public abstract class AbstractCronMethodTrigger extends AbstractMethodRunnable implements Runnable, Comparable<AbstractCronMethodTrigger> {

    protected final CronExpression cronExpress;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final Monitor monitor = new Monitor();
    protected final AtomicBoolean runEnd = new AtomicBoolean(true);
    protected long nextRunTime = -1;

    public AbstractCronMethodTrigger(Method method, CronExpression cronExpress) {
        super(method);
        this.sourceLine = StackUtils.stack(0, 1);
        this.cronExpress = cronExpress;
    }

    public int index() {
        return Const.SORT_DEFAULT;
    }

    public boolean scheduleAtFixedRate() {
        return false;
    }

    /** 查询当前时间的下一次开启时间 */
    public long nowNextTime() {
        return CronExpressionUtil.nextMillis(cronExpress);
    }

    public long getNextRunTime() {
        if (nextRunTime == -1) {
            this.nextRunTime = nowNextTime();
        }
        return nextRunTime;
    }

    /** 检查时间是否满足 */
    public boolean checkRunTime(long millis) {
        return millis >= getNextRunTime();
    }

    public boolean isAsync() {
        return getExecutorWith() != null;
    }

    @Override public void run() {
        try {
            super.run();
        } catch (Exception e) {
            log.error("执行：{}", this, e);
        } finally {
            /*标记为执行完成*/
            monitor.sync(() -> runEnd.set(true));
        }
    }

    @Override
    public int compareTo(AbstractCronMethodTrigger o) {
        if (this.index() != o.index())
            return Integer.compare(this.index(), o.index());
        if (!Objects.equals(this.getClass().getName(), o.getClass().getName())) {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
        return Integer.compare(this.hashCode(), o.hashCode());
    }

}
