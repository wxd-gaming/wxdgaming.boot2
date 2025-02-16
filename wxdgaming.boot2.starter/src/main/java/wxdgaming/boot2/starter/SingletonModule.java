package wxdgaming.boot2.starter;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.BaseModule;
import wxdgaming.boot2.core.reflect.ReflectContext;

/**
 * 构建Singleton
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-16 10:35
 **/
public class SingletonModule extends BaseModule {

    public SingletonModule(ReflectContext reflectContext) {
        super(reflectContext);
    }


    @Override protected void bind() throws Throwable {
        bindClassWithAnnotated(Singleton.class);
    }

}
