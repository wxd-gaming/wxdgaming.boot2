package wxdgaming.logserver.plugin;

import wxdgaming.boot2.core.ApplicationContextProvider;

/**
 * 插件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 16:49
 */
public abstract class AbstractPlugin {

    /** 执行间隔时间 */
    public abstract String cron();

    public abstract void trigger(ApplicationContextProvider runApplication);

}
