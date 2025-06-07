package wxdgaming.boot2.core;

import wxdgaming.boot2.core.reflect.ReflectContext;

import java.lang.annotation.Annotation;

/**
 * 服务使用
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:14
 **/
public abstract class ServiceGuiceModule extends GuiceModuleBase {

    public ServiceGuiceModule(ReflectContext reflectContext) {
        super(reflectContext);
    }

    @Override public void bindClassWithAnnotated(Class<? extends Annotation> annotation) {
    }
}
