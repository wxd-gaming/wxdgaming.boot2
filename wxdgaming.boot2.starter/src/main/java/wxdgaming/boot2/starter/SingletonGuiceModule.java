package wxdgaming.boot2.starter;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.GuiceModuleBase;
import wxdgaming.boot2.core.reflect.ReflectProvider;

/**
 * 构建Singleton
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 10:35
 **/
public class SingletonGuiceModule extends GuiceModuleBase {

    private final Class<?>[] classes;

    public SingletonGuiceModule(ReflectProvider reflectProvider, Class<?>... classes) {
        super(reflectProvider);
        this.classes = classes;
    }


    @Override protected void bind() throws Throwable {
        bindClassWithAnnotated(Singleton.class);
        for (Class<?> clazz : classes) {
            bindSingleton(clazz);
        }
    }

}
