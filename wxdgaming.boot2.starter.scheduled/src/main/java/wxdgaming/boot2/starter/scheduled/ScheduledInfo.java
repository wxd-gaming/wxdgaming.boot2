package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.timer.CronExpress;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.lang.reflect.Method;
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
public class ScheduledInfo extends ExecutorEvent implements Runnable, IExecutorQueue, Comparable<ScheduledInfo> {

    protected String name;
    protected int index;
    protected final Object instance;
    protected final Method method;
    protected JavassistProxy scheduledProxy;
    /** 和method是互斥的 */
    protected Runnable scheduledTask;
    protected final CronExpress cronExpress;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final AtomicBoolean runEnd = new AtomicBoolean(true);
    protected long nextRunTime = -1;

    public ScheduledInfo(Object instance, Method method, Scheduled scheduled) {
        super(method);
        this.instance = instance;
        this.method = method;

        scheduledProxy = JavassistProxy.of(instance, method);

        if (StringUtils.isNotBlank(scheduled.name())) {
            this.name = "[scheduled-job] " + scheduled.name();
        } else {
            this.name = "[scheduled-job] " + instance.getClass().getName() + "." + method.getName();
        }

        final Order orderAnn = AnnUtil.ann(method, Order.class);
        this.index = orderAnn == null ? Const.SORT_DEFAULT : orderAnn.value();
        this.scheduleAtFixedRate = scheduled.scheduleAtFixedRate();
        this.cronExpress = new CronExpress(scheduled.value(), TimeUnit.SECONDS, 0);
    }


    /**
     * 秒 分 时 日 月 星期 年
     * <p> {@code * * * * * * * }
     * <p> 下面以 秒 配置举例
     * <p> * 或者 ? 无限制,
     * <p> 数字是 指定秒执行
     * <p> 0-5 第 0 秒 到 第 5 秒执行 每秒执行
     * <p> 0,5 第 0 秒 和 第 5 秒 各执行一次
     * <p> {@code *}/5 秒 % 5 == 0 执行
     * <p> 5/5 第五秒之后 每5秒执行一次
     * <p> 秒 0-59
     * <p> 分 0-59
     * <p> 时 0-23
     * <p> 日 1-28 or 29 or 30 or 31
     * <p> 月 1-12
     * <p> 星期 1-7 Mon Tues Wed Thur Fri Sat Sun
     * <p> 年 1970 - 2199
     */
    public ScheduledInfo(Runnable scheduledTask, String scheduledName, String scheduled, boolean scheduleAtFixedRate) {
        this.instance = null;
        this.method = null;
        this.scheduledTask = scheduledTask;
        this.name = "[timer-job] " + scheduledTask.getClass() + "-" + scheduledName;

        this.index = Const.SORT_DEFAULT;
        this.scheduleAtFixedRate = scheduleAtFixedRate;
        this.cronExpress = new CronExpress(scheduled, TimeUnit.SECONDS, 0);
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
        return executorWith != null;
    }

    @Override public String getStack() {
        return this.name;
    }

    @Override public void onEvent() throws Exception {
        try {
            if (scheduledProxy != null) {
                scheduledProxy.proxyInvoke(Objects.ZERO_ARRAY);
            } else {
                scheduledTask.run();
            }
        } catch (Throwable throwable) {
            String msg = "执行：" + this.name;
            GlobalUtil.exception(msg, throwable);
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
    public int compareTo(ScheduledInfo o) {
        return Integer.compare(this.index, o.index);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledInfo that = (ScheduledInfo) o;
        return getInstance().getClass().getName().equals(that.getInstance().getClass().getName())
               && MethodUtil.methodFullName(getMethod()).equals(MethodUtil.methodFullName(that.getMethod()));
    }

    @Override public int hashCode() {
        int result = getInstance().hashCode();
        result = 31 * result + getMethod().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
