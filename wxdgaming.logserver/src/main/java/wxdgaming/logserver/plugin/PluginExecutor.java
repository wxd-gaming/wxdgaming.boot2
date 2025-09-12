package wxdgaming.logserver.plugin;

import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.starter.scheduled.AbstractCronTrigger;

import java.util.function.Supplier;

/**
 * 插件执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 16:54
 **/
public class PluginExecutor extends AbstractCronTrigger {

    private final Supplier<ApplicationContextProvider> applicationProvider;
    private final AbstractPlugin abstractPlugin;

    public PluginExecutor(Supplier<ApplicationContextProvider> applicationProvider, AbstractPlugin abstractPlugin) {
        super(abstractPlugin.cron());
        this.applicationProvider = applicationProvider;
        this.abstractPlugin = abstractPlugin;
    }

    @Override public void onEvent() throws Exception {
        abstractPlugin.trigger(applicationProvider.get());
    }

    @Override public String getStack() {
        return abstractPlugin.getClass().getSimpleName() + "#trigger()";
    }
}
