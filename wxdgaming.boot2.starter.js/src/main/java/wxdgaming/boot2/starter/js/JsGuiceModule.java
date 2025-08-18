package wxdgaming.boot2.starter.js;

import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * pgsql 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:42
 **/
public class JsGuiceModule extends ServiceGuiceModule {


    public JsGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {

    }

}
