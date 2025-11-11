package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.executor.IExecutorQueue;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.MethodProvider;
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
    final MethodProvider methodProvider;
    protected int index;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;

    public ScheduledInfo(MethodProvider methodProvider, Scheduled scheduled) {
        super(methodProvider.getMethod(), new CronExpress(scheduled.value(), TimeUnit.SECONDS, 0));
        this.methodProvider = methodProvider;
        if (StringUtils.isNotBlank(scheduled.name())) {
            this.name = "[scheduled-job] " + scheduled.name();
        } else {
            this.name = "[scheduled-job] " + methodProvider.toString();
        }

        final Order orderAnn = AnnUtil.ann(methodProvider.getMethod(), Order.class);
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

    @Override public boolean isIgnoreRunTimeRecord() {
        return true;
    }

    @Override public String getStack() {
        return this.name;
    }

    @Override public void onEvent() throws Exception {
        try {
            methodProvider.invoke(Objects.ZERO_ARRAY);
        } catch (Throwable throwable) {
            String msg = "执行：" + this.name;
            log.error(msg, throwable);
        } finally {
            /*标记为执行完成*/
            monitor.sync(() -> runEnd.set(true));
        }
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledInfo that = (ScheduledInfo) o;
        return methodProvider.getInstance().getClass().getName().equals(that.methodProvider.getInstance().getClass().getName())
               && MethodUtil.methodFullName(methodProvider.getMethod()).equals(MethodUtil.methodFullName(that.methodProvider.getMethod()));
    }

    @Override public int hashCode() {
        int result = methodProvider.getInstance().hashCode();
        result = 31 * result + methodProvider.getMethod().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
