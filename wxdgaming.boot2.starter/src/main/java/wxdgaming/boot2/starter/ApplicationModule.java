package wxdgaming.boot2.starter;

import wxdgaming.boot2.core.BaseModule;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ExecutorUtil;

/**
 * 线程模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:57
 **/
@Sort(1)
class ApplicationModule extends BaseModule {

    public ApplicationModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        ExecutorConfig executorConfig = BootConfig.getIns().getExecutorConfig();
        ExecutorUtil.getInstance().init(executorConfig);
        bindInstance(ExecutorUtil.getInstance());
    }

}
