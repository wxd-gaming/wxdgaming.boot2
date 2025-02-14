package wxdgaming.boot2.starter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import wxdgaming.boot2.core.RunApplication;

/**
 * 运行类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:55
 **/
public final class RunApplicationMain extends RunApplication {

    @Inject
    public RunApplicationMain(Injector injector) {
        super(injector);
    }

    @Override public void init() {
        super.init();
    }
}
