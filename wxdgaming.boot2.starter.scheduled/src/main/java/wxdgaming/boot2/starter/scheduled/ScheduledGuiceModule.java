package wxdgaming.boot2.starter.scheduled;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:30
 **/
public class ScheduledGuiceModule extends ServiceGuiceModule {

    public static final ExecutorConfig DEFAULT_INSTANCE = new ExecutorConfig(1, 1000, QueuePolicyConst.AbortPolicy);

    public ScheduledGuiceModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        ExecutorConfig nestedValue = BootConfig.getIns().getNestedValue("executor.scheduled", ExecutorConfig.class, DEFAULT_INSTANCE);
        ScheduledService scheduledService = new ScheduledService(nestedValue);
        bindInstance(scheduledService);
    }
}
