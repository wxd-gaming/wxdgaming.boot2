package wxdgaming.boot2.core;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Qualifier;
import wxdgaming.boot2.core.ann.Shutdown;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.reflect.GuiceBeanProvider;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.core.util.JvmUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

/**
 * 运行类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 16:55
 **/
@Slf4j
@Getter
public abstract class RunApplication {

    private final Injector injector;
    private final HashMap<Key<?>, Object> hashMap = new HashMap<>();
    private GuiceBeanProvider guiceBeanProvider;

    public RunApplication(Injector injector) {
        this.injector = injector;
    }

    protected void init() {

        Map<Key<?>, Binding<?>> allBindings = new HashMap<>();
        allBindings(getInjector(), allBindings);
        final Set<Key<?>> keys = allBindings.keySet();
        try {
            for (Key<?> key : keys) {
                final Object instance = getInjector().getInstance(key);
                Object oldPut = hashMap.put(key, instance);
                AssertUtil.assertTrue(oldPut == null, "bean:%s is repeat, %s %s", key, instance, oldPut);
            }
        } catch (Exception e) {
            throw Throw.of(e);
        }

        HashSet<Object> beanList = new HashSet<>(hashMap.values());
        guiceBeanProvider = new GuiceBeanProvider(this, beanList);
        guiceBeanProvider.withFieldAnnotated(Value.class).forEach(fieldProvider -> {
            Value annotation = fieldProvider.getField().getAnnotation(Value.class);
            if (annotation == null) return;
            Object valued = BootConfig.getIns().value(annotation, fieldProvider.getField().getType());
            try {
                if (valued != null) {
                    fieldProvider.invoke(valued);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void registerShutdownHook() {
        JvmUtil.addShutdownHook(this::stop);
    }

    public void stop() {
        if (!GlobalUtil.Exiting.compareAndSet(false, true)) return;
        System.out.println("--------------------------shutdown---------------------------");
        executeMethodWithAnnotatedException(Shutdown.class);
        classWithSuper(AutoCloseable.class)
                .forEach(closeable -> {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        log.error("关闭异常: {}", closeable.getClass(), e);
                    }
                });
        ExecutorFactory.getExecutorMonitor().getExit().set(true);
    }

    void allBindings(Injector context, Map<Key<?>, Binding<?>> allBindings) {
        if (context.getParent() != null) {
            allBindings(context.getParent(), allBindings);
        }
        allBindings.putAll(context.getAllBindings());
    }

    /**
     * 返回给定注入类型的相应实例;等同于 getProvider(type).get()。如果可行，请避免使用此方法，以便让 Guice 提前注入您的依赖项。
     *
     * @param clazz 查找的实例类
     * @param <T>   实例对象
     * @return 实例对象
     * @throws com.google.inject.ConfigurationException – 如果此 injector 找不到或创建提供程序。
     * @throws com.google.inject.ProvisionException     – 如果在提供实例时出现运行时故障。
     */
    public <T> T getInstance(Class<T> clazz) {
        Key<?> key = Key.get(clazz);
        return getInstance(key);
    }

    /**
     * 返回给定注入类型的相应实例;等同于 getProvider(type).get()。如果可行，请避免使用此方法，以便让 Guice 提前注入您的依赖项。
     *
     * @param type 查找的实例类
     * @param <T>  实例对象
     * @return 实例对象
     * @throws com.google.inject.ConfigurationException – 如果此 injector 找不到或创建提供程序。
     * @throws com.google.inject.ProvisionException     – 如果在提供实例时出现运行时故障。
     */
    public <T> T getInstance(Type type) {
        Key<?> key = Key.get(type);
        return getInstance(key);
    }

    /**
     * 返回给定注入类型的相应实例;等同于 getProvider(type).get()。如果可行，请避免使用此方法，以便让 Guice 提前注入您的依赖项。
     *
     * @param clazz 查找的实例类
     * @param name  绑定时候指定的名称
     * @param <T>   实例对象
     * @return 实例对象
     * @throws com.google.inject.ConfigurationException – 如果此 injector 找不到或创建提供程序。
     * @throws com.google.inject.ProvisionException     – 如果在提供实例时出现运行时故障。
     */
    public <T> T getInstance(Type clazz, String name) {
        Key<?> key = Key.get(clazz, Names.named(name));
        return getInstance(key);
    }

    /**
     * 返回给定注入类型的相应实例;等同于 getProvider(type).get()。如果可行，请避免使用此方法，以便让 Guice 提前注入您的依赖项。
     *
     * @param clazz 查找的实例类
     * @param name  绑定时候指定的名称
     * @param <T>   实例对象
     * @return 实例对象
     * @throws com.google.inject.ConfigurationException – 如果此 injector 找不到或创建提供程序。
     * @throws com.google.inject.ProvisionException     – 如果在提供实例时出现运行时故障。
     */
    public <T> T getInstance(Type clazz, Named name) {
        Key<?> key = Key.get(clazz, name);
        return getInstance(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Key<?> key) {
        return (T) hashMap.get(key);
    }

    /** 获取参数 */
    public <T> T getInstanceByParameter(Parameter parameter) {
        Type parameterizedType = parameter.getParameterizedType();
        {
            Named named = parameter.getAnnotation(Named.class);
            if (named != null) {
                return getInstance(parameterizedType, named);
            }
        }
        T instance = getInstance(parameterizedType);
        if (instance == null) {
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null && qualifier.required()) {
                throw new RuntimeException("bean:" + parameterizedType + " is not bind");
            }
        }
        return instance;
    }

    /** 通过接口或者父类查找 实现类 */
    public <T> Stream<T> classWithSuper(Class<T> clazz) {
        return guiceBeanProvider.classWithSuper(clazz);
    }

    /** 查找添加了某个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return guiceBeanProvider.classWithAnnotated(annotation);
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Collection<GuiceBeanProvider.ProviderMethod> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return guiceBeanProvider.withMethodAnnotated(annotation);
    }

    /** 执行循环过程中某一个函数执行失败中断执行 */
    public void executeMethodWithAnnotated(Class<? extends Annotation> annotation, Object... args) {
        guiceBeanProvider.executeMethodWithAnnotated(annotation, args);
    }

    /** 执行循环过程中某一个函数执行失败会继续执行其它函数 */
    public void executeMethodWithAnnotatedException(Class<? extends Annotation> annotation, Object... args) {
        guiceBeanProvider.executeMethodWithAnnotatedException(annotation, args);
    }


}
