package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.timer.CronExpress;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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
public class ScheduledInfo extends AbstractCronTrigger implements Runnable, IExecutorQueue {

    protected String name;
    protected final Object instance;
    protected final Method method;
    protected JavassistProxy scheduledProxy;
    protected int index;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;

    public ScheduledInfo(Object instance, Method method, Scheduled scheduled) {
        super(method, new CronExpress(scheduled.value(), TimeUnit.SECONDS, 0));
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
    }

    @Override public int index() {
        return getIndex();
    }

    @Override public boolean scheduleAtFixedRate() {
        return isScheduleAtFixedRate();
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
