package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.locks.Monitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * 反射类信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-17 10:22
 **/
@Slf4j
@Getter
public class ReflectClassProvider {

    private static final Monitor MONITOR = new Monitor();
    private static final WeakHashMap<Class<?>, ReflectClassProvider> WEAK_HASH_MAP = new WeakHashMap<>();

    /** 带过期缓存，避免同一个 class 多次反射 */
    public static ReflectClassProvider build(Class<?> clazz) {
        MONITOR.lock();
        try {
            return WEAK_HASH_MAP.computeIfAbsent(clazz, ReflectClassProvider::new);
        } finally {
            MONITOR.unlock();
        }
    }

    private final Class<?> clazz;
    private Map<String, ReflectFieldProvider> fieldMap = null;
    private Map<Field, ReflectFieldProvider> fieldProviderMap = null;
    private Map<String, Method> methodMap;

    public ReflectClassProvider(Class<?> clazz) {
        this.clazz = clazz;
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return AnnUtil.hasAnn(clazz, annotation);
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return clazz.isAssignableFrom(cls);
    }

    @SuppressWarnings("unchecked")
    public <T> T cast(Object obj) {
        return (T) clazz.cast(obj);
    }

    public Map<String, ReflectFieldProvider> getFieldMap() {
        if (fieldMap == null) {
            Map<String, ReflectFieldProvider> tmpFieldMap = new LinkedHashMap<>();
            Map<Field, ReflectFieldProvider> tmpFieldProviderMap = new LinkedHashMap<>();
            Map<String, Field> fields = FieldUtil.getFields(false, clazz);
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                ReflectFieldProvider reflectFieldProvider = new ReflectFieldProvider(this.clazz, entry.getValue());
                tmpFieldMap.put(entry.getKey(), reflectFieldProvider);
                tmpFieldProviderMap.put(entry.getValue(), reflectFieldProvider);
            }
            fieldMap = tmpFieldMap;
            fieldProviderMap = tmpFieldProviderMap;
        }
        return fieldMap;
    }

    public Map<String, Method> getMethodMap() {
        if (methodMap == null) {
            this.methodMap = MethodUtil.readAllMethod(false, clazz);
        }
        return methodMap;
    }

    public Stream<Method> methodStream() {
        return getMethodMap().values().stream();
    }

    public Stream<Method> methodStreamWithAnnotation(Class<? extends Annotation> annotation) {
        return getMethodMap().values().stream().filter(method -> method.isAnnotationPresent(annotation));
    }

    public Method findMethod(String methodName, Class<?>... parameters) {
        StringBuilder fullName = new StringBuilder(methodName);
        for (Class<?> parameter : parameters) {
            fullName.append("_").append(parameter.getSimpleName());
        }
        return getMethodMap().get(fullName.toString());
    }

    public ReflectFieldProvider getFieldContext(String fieldName) {
        return getFieldMap().get(fieldName);
    }

    public ReflectFieldProvider getFieldContext(Field field) {
        return fieldProviderMap.computeIfAbsent(field, l -> new ReflectFieldProvider(this.clazz, field));
    }

}
