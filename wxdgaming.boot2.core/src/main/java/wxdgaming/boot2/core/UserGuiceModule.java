package wxdgaming.boot2.core;

import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * 服务使用
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:14
 **/
public abstract class UserGuiceModule extends GuiceModuleBase {

    public UserGuiceModule(ReflectProvider reflectProvider) {
        super(reflectProvider);
    }

}
