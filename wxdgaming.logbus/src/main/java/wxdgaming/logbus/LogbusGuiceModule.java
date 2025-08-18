package wxdgaming.logbus;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * 驱动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:09
 **/
public class LogbusGuiceModule extends ServiceGuiceModule {

    public LogbusGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        LogBusProperties logBusProperties = BootConfig.getIns().getNestedValue("logbus", LogBusProperties.class);
        bindInstance(logBusProperties);
    }

}
