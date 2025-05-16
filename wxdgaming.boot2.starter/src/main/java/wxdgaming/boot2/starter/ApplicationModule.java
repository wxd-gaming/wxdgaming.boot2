package wxdgaming.boot2.starter;

import wxdgaming.boot2.core.BaseModule;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorService;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * 线程模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:57
 **/
@Order(1)
class ApplicationModule extends BaseModule {

    public ApplicationModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        ExecutorService executorServiceBasic = ExecutorFactory.EXECUTOR_SERVICE_BASIC;
    }

}
