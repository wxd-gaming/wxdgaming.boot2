package wxdgaming.boot2.starter.scheduled;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:30
 **/
public class ScheduledModule extends ServiceModule {

    public ScheduledModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        ScheduledConfig scheduledConfig = BootConfig.getIns().getNestedValue("scheduled", ScheduledConfig.class);
        if (scheduledConfig == null)
            scheduledConfig = new ScheduledConfig();
        ScheduledService scheduledService = new ScheduledService(scheduledConfig);
        bindInstance(scheduledService);
    }
}
