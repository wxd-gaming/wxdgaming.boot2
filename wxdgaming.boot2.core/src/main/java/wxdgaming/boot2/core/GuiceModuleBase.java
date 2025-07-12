package wxdgaming.boot2.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.reflect.ReflectProvider;

import java.lang.annotation.Annotation;

/**
 * 基础模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-09-15 10:12
 **/
@Slf4j
@Getter
public abstract class GuiceModuleBase extends AbstractModule {

    final ReflectProvider reflectProvider;

    public GuiceModuleBase(ReflectProvider reflectProvider) {
        this.reflectProvider = reflectProvider;
    }

    /** 绑定指定注解 */
    public void bindClassWithAnnotated(Class<? extends Annotation> annotation) {
        reflectProvider.classWithAnnotated(annotation)
                .sorted(ReflectProvider.ComparatorClassBySort)
                .forEach(this::bindSingleton);
    }

    public void bindSingleton(Class<?> clazz) {
        //        log.debug("bind clazz {} {} clazz={}", this.getClass().getName(), this.hashCode(), clazz);
        bind(clazz).in(Singleton.class);
    }

    public <R> void bindSingleton(Class<R> father, Class<? extends R> son) {
        //        log.debug("bind clazz father to son {} {} father={} son={}", this.getClass().getName(), this.hashCode(), father, son);
        bind(father).to(son).in(Singleton.class);
    }

    public void bindInstance(Object instance) {
        Class aClass = instance.getClass();
        bindInstance(aClass, instance);
    }

    @SuppressWarnings({"unchecked", "rawtype"})
    public void bindInstance(Class clazz, Object instance) {
        bind(clazz).toInstance(instance);
    }

    @SuppressWarnings({"unchecked", "rawtype"})
    public <T> void bindInstance(String name, T instance) {
        Class<T> aClass = (Class<T>) instance.getClass();
        bind(aClass).annotatedWith(Names.named(name)).toInstance(instance);
    }

    @SuppressWarnings({"unchecked", "rawtype"})
    public <T> void bindInstance(Class clazz, String name, T instance) {
        bind(clazz).annotatedWith(Names.named(name)).toInstance(instance);
    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
        binder().requireExactBindingAnnotations();
        // binder().disableCircularProxies();/*禁用循环依赖*/

        /*绑定实现*/
        bindListener(Matchers.any(), new PostConstructListener());

        try {
            bind();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // @Provides
    // @Singleton
    // public Injector injector(Injector injector) {
    //     return injector;
    // }

    protected abstract void bind() throws Throwable;

}
