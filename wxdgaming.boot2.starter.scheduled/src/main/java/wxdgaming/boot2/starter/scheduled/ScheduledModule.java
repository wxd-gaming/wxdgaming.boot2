package wxdgaming.boot2.starter.scheduled;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.threading.ExecutorConfig;

/**
 * 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:30
 **/
public class ScheduledModule extends ServiceModule {

    public static final ExecutorConfig DEFAULT_INSTANCE = new ExecutorConfig(1, 1, 5000);

    public ScheduledModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        ExecutorConfig nestedValue = BootConfig.getIns().getNestedValue("executor.scheduled", ExecutorConfig.class, DEFAULT_INSTANCE);
        ScheduledService scheduledService = new ScheduledService(nestedValue);
        bindInstance(scheduledService);
    }
}
