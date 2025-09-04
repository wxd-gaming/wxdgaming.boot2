package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.core.timer.CronExpress;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

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
    final ApplicationContextProvider.ProviderMethod providerMethod;
    protected int index;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;

    public ScheduledInfo(ApplicationContextProvider.ProviderMethod providerMethod, Scheduled scheduled) {
        super(providerMethod.getMethod(), new CronExpress(scheduled.value(), TimeUnit.SECONDS, 0));
        this.providerMethod = providerMethod;
        if (StringUtils.isNotBlank(scheduled.name())) {
            this.name = "[scheduled-job] " + scheduled.name();
        } else {
            this.name = "[scheduled-job] " + providerMethod.toString();
        }

        final Order orderAnn = AnnUtil.ann(providerMethod.getMethod(), Order.class);
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
            providerMethod.invoke(Objects.ZERO_ARRAY);
        } catch (Throwable throwable) {
            String msg = "执行：" + this.name;
            log.error(msg, throwable);
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
        return providerMethod.getBean().getClass().getName().equals(that.providerMethod.getBean().getClass().getName())
               && MethodUtil.methodFullName(providerMethod.getMethod()).equals(MethodUtil.methodFullName(that.providerMethod.getMethod()));
    }

    @Override public int hashCode() {
        int result = providerMethod.getBean().hashCode();
        result = 31 * result + providerMethod.getMethod().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
