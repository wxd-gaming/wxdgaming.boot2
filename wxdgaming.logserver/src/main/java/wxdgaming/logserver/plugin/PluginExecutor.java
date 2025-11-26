package wxdgaming.logserver.plugin;

import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.executor.ExecutorLog;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.proxy.AopProxyUtil;
import wxdgaming.boot2.starter.scheduled.AbstractCronTrigger;

import java.lang.annotation.Annotation;
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
        this.setExecutorWith(new ExecutorWith(){
            @Override public Class<? extends Annotation> annotationType() {
                return ExecutorWith.class;
            }

            @Override public String queueName() {
                return "PluginExecutor";
            }

            @Override public String threadName() {
                return "";
            }

            @Override public boolean useVirtualThread() {
                return true;
            }
        });
    }

    @Override public ExecutorLog getExecutorLog() {
        Object targetObject = AopProxyUtil.getTargetObject(abstractPlugin);
        ExecutorLog annotation = targetObject.getClass().getAnnotation(ExecutorLog.class);
        if (annotation != null) return annotation;
        return super.getExecutorLog();
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        return super.isIgnoreRunTimeRecord();
    }

    @Override public boolean isAsync() {
        return true;
    }

    @Override public void onEvent() throws Exception {
        abstractPlugin.trigger(applicationProvider.get());
    }

    @Override public String getStack() {
        return abstractPlugin.getClass().getSimpleName() + "#trigger()";
    }
}
