package wxdgaming.boot2.starter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-09-15 10:12
 **/
@Slf4j
@Getter
abstract class BaseModule extends AbstractModule {

    public BaseModule() {
    }

    public void bindSingleton(Class<?> clazz) {
        log.debug("bind {} {} clazz={}", this.getClass().getName(), this.hashCode(), clazz);
        bind(clazz).in(Singleton.class);
    }

    public <R> void bindSingleton(Class<R> father, Class<? extends R> son) {
        log.debug("bind {} {} father={} bind son={}", this.getClass().getName(), this.hashCode(), father, son);
        bind(father).to(son).in(Singleton.class);
    }

    public <B> void bindSingleton(Class<B> clazz, B instance) {
        log.debug("bind {} {} clazz={} bind instance={}", this.getClass().getName(), this.hashCode(), clazz, instance.getClass());
        bind(clazz).toInstance(instance);
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        binder().requireExactBindingAnnotations();
        // binder().disableCircularProxies();/*禁用循环依赖*/

        try {
            bind();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void bind() throws Throwable;

}
