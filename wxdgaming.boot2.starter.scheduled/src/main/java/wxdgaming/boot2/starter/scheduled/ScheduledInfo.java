package wxdgaming.boot2.starter.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.support.CronExpression;
import wxdgaming.boot2.core.Const;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.reflect.InstanceMethodProvider;
import wxdgaming.boot2.core.reflect.MethodUtil;
import wxdgaming.boot2.starter.scheduled.ann.Scheduled;

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
public class ScheduledInfo extends AbstractCronMethodTrigger implements Runnable {

    protected String name;
    final InstanceMethodProvider instanceMethodProvider;
    protected int index;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;

    public ScheduledInfo(InstanceMethodProvider instanceMethodProvider, Scheduled scheduled) {
        super(instanceMethodProvider.getMethod(), CronExpression.parse(scheduled.value()));
        this.instanceMethodProvider = instanceMethodProvider;
        if (StringUtils.isNotBlank(scheduled.name())) {
            this.name = "[scheduled-job] " + scheduled.name();
        } else {
            this.name = "[scheduled-job] " + instanceMethodProvider.toString();
        }
        this.sourceLine = this.name;
        final Order orderAnn = AnnUtil.ann(instanceMethodProvider.getMethod(), Order.class);
        this.index = orderAnn == null ? Const.SORT_DEFAULT : orderAnn.value();
        this.scheduleAtFixedRate = scheduled.scheduleAtFixedRate();
    }

    @Override public int index() {
        return getIndex();
    }

    @Override public boolean scheduleAtFixedRate() {
        return isScheduleAtFixedRate();
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        return true;
    }

    @Override public void onEvent() {
        try {
            instanceMethodProvider.invoke(Objects.ZERO_ARRAY);
        } catch (Throwable throwable) {
            String msg = "执行：" + this.name;
            log.error(msg, throwable);
        }
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledInfo that = (ScheduledInfo) o;
        return instanceMethodProvider.getInstance().getClass().getName().equals(that.instanceMethodProvider.getInstance().getClass().getName())
               && MethodUtil.methodFullName(instanceMethodProvider.getMethod()).equals(MethodUtil.methodFullName(that.instanceMethodProvider.getMethod()));
    }

    @Override public int hashCode() {
        int result = instanceMethodProvider.getInstance().hashCode();
        result = 31 * result + instanceMethodProvider.getMethod().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
