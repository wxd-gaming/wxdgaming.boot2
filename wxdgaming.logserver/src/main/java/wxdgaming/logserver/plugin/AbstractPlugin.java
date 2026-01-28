package wxdgaming.logserver.plugin;

import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.executor.ExecutorLog;
import wxdgaming.boot2.core.executor.ExecutorWith;
import wxdgaming.boot2.core.proxy.AopProxyUtil;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.runtime.IgnoreRunTimeRecord;

/**
 * 插件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 16:49
 */
public abstract class AbstractPlugin {

    public ExecutorLog getExecutorLog() {
        return AnnUtil.ann(this.getClass(), ExecutorLog.class);
    }

    public ExecutorWith getExecutorWith() {
        return AnnUtil.ann(this.getClass(), ExecutorWith.class);
    }

    public IgnoreRunTimeRecord getIgnoreRunTimeRecord() {
        return AnnUtil.ann(this.getClass(), IgnoreRunTimeRecord.class);
    }

    /** 执行间隔时间 */
    public abstract String cron();

    public abstract void trigger(ApplicationContextProvider runApplication);

}
