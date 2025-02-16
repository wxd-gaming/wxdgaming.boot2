package wxdgaming.boot2.core;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import lombok.Getter;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.reflect.GuiceReflectContext;

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
    private GuiceReflectContext reflectContext;

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

        reflectContext = new GuiceReflectContext(this, hashMap.values());
        reflectContext.stream().forEach(content -> {
            content.fieldWithAnnotated(Value.class).forEach(field -> {
                Object valued = BootConfig.getIns().value(field.getAnnotation(Value.class), field.getType());
                try {
                    if (valued != null) {
                        field.set(content.getT(), valued);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    static void allBindings(Injector context, Map<Key<?>, Binding<?>> allBindings) {
        if (context.getParent() != null) {
            allBindings(context.getParent(), allBindings);
        }
        allBindings.putAll(context.getAllBindings());
    }

    public <T> T getInstance(Class<T> clazz) {
        return reflectContext.classWithSuper(clazz).findFirst().orElse(null);
    }

    public <T> Stream<T> classWithSuper(Class<T> clazz) {
        return reflectContext.classWithSuper(clazz);
    }

}
