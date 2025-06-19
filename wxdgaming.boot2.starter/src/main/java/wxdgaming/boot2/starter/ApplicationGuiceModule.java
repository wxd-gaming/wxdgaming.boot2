package wxdgaming.boot2.starter;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.GuiceModuleBase;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * 线程模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:57
 **/
@Order(1)
class ApplicationGuiceModule extends GuiceModuleBase {

    public ApplicationGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        bind(RunApplicationMain.class);
        bindInstance(BootConfig.getIns());
        bindInstance(int.class, "sid", BootConfig.getIns().sid());
        bindInstance(boolean.class, "debug", BootConfig.getIns().isDebug());
    }

}
