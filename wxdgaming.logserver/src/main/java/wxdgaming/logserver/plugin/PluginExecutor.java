package wxdgaming.logserver.plugin;

import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.executor.CronExpressionUtil;
import wxdgaming.boot2.core.executor.ExecutorLog;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.runtime.IgnoreRunTimeRecord;
import wxdgaming.boot2.starter.scheduled.AbstractCronMethodTrigger;

import java.util.function.Supplier;

/**
 * 插件执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 16:54
 **/
public class PluginExecutor extends AbstractCronMethodTrigger {

    private final Supplier<ApplicationContextProvider> applicationProvider;
    private final AbstractPlugin abstractPlugin;

    public PluginExecutor(Supplier<ApplicationContextProvider> applicationProvider, AbstractPlugin abstractPlugin) {
        super(null, CronExpressionUtil.parse(abstractPlugin.cron()));
        this.applicationProvider = applicationProvider;
        this.abstractPlugin = abstractPlugin;
    }

    @Override public ExecutorLog getExecutorLog() {
        return abstractPlugin.getExecutorLog();
    }

    @Override public ExecutorWith getExecutorWith() {
        return abstractPlugin.getExecutorWith();
    }

    @Override public IgnoreRunTimeRecord getIgnoreRunTimeRecord() {
        return abstractPlugin.getIgnoreRunTimeRecord();
    }

    @Override public boolean isAsync() {
        return getExecutorWith() != null;
    }

    @Override public void onEvent() throws Exception {
        abstractPlugin.trigger(applicationProvider.get());
    }

    @Override public String getSourceLine() {
        return abstractPlugin.getClass().getSimpleName() + "#trigger()";
    }
}
