package wxdgaming.game.login;

import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ServiceGuiceModule;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * 驱动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:09
 **/
public class LoginServiceGuiceModule extends ServiceGuiceModule {

    public LoginServiceGuiceModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override protected void bind() throws Throwable {
        bindInstance(LoginConfig.class, BootConfig.getIns().getNestedValue("login", LoginConfig.class));
    }

}
