package wxdgaming.boot2.starter;

import wxdgaming.boot2.core.BaseModule;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.reflect.ReflectContext;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.threading.ExecutorUtilImpl;

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
        ExecutorUtil impl = ExecutorUtilImpl.impl();
        bindInstance(impl);
    }

}
