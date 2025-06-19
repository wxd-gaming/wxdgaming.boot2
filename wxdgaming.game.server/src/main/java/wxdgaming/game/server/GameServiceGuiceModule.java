package wxdgaming.game.server;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectProvider;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.server.bean.BackendConfig;

/**
 * 驱动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:09
 **/
public class GameServiceGuiceModule extends ServiceGuiceModule {

    public GameServiceGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

    @Override protected void bind() throws Throwable {
        bindInstance(int.class, "serverType", BootConfig.getIns().getIntValue("serverType"));
        bindInstance(BackendConfig.class, BootConfig.getIns().getNestedValue("backends", BackendConfig.class));
    }

}
