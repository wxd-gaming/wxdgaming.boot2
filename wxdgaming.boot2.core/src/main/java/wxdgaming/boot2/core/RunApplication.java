package wxdgaming.boot2.core;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 运行类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 16:55
 **/
@Getter
public abstract class RunApplication {

    private final Injector injector;
    private GuiceReflectContext guiceReflectContext;

    public RunApplication(Injector injector) {
        this.injector = injector;
    }

    protected void init() {

        HashMap<String, Object> hashMap = new HashMap<>();
        Map<Key<?>, Binding<?>> allBindings = new HashMap<>();
        allBindings(getInjector(), allBindings);
        final Set<Key<?>> keys = allBindings.keySet();
        try {
            for (Key<?> key : keys) {
                final Object instance = getInjector().getInstance(key);
                hashMap.put(instance.getClass().getName(), instance);
            }
        } catch (Exception e) {
            throw Throw.of(e);
        }

        guiceReflectContext = new GuiceReflectContext(this, hashMap.values());
        guiceReflectContext.stream().forEach(content -> {
            content.fieldWithAnnotated(Value.class).forEach(field -> {
                Object valued = BootConfig.getIns().value(field.getAnnotation(Value.class), field.getType());
                try {
                    if (valued != null) {
                        field.set(content.getInstance(), valued);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        });
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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-19 11:02
     */
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    /**
     * 通过接口或者父类查找 实现类
     *
     * @param clazz 查找的实例类
     * @param <T>   实例对象
     * @return 实例对象
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-19 11:02
     */
    public <T> Stream<T> classWithSuper(Class<T> clazz) {
        return guiceReflectContext.classWithSuper(clazz);
    }

    /**
     * 查找添加了某个注解的类
     *
     * @param annotation 注解
     * @return 类流
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-02-19 11:04
     */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return guiceReflectContext.classWithAnnotated(annotation);
    }

    /** 执行包含某个注解的方法 */
    public void executeMethodWithAnnotated(Class<? extends Annotation> annotation) {
        guiceReflectContext.executeMethodWithAnnotated(annotation);
    }

}
