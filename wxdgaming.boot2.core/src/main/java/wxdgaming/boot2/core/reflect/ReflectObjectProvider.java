package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 反射类信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-17 10:22
 **/
@Slf4j
@Getter
public class ReflectObjectProvider implements Comparable<ReflectObjectProvider> {

    private final Object instance;
    private final Class<?> clazz;
    private Map<String, FieldProvider> fieldMap;
    private Map<Field, FieldProvider> fieldProviderMap;
    private Map<String, MethodProvider> methodMap;

    public ReflectObjectProvider(Object instance) {
        this.instance = instance;
        this.clazz = instance.getClass();
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return clazz.isAssignableFrom(cls);
    }

    @Override public int compareTo(ReflectObjectProvider o) {
        int o1Annotation = AnnUtil.orderValue(this.instance.getClass());
        int o2Annotation = AnnUtil.orderValue(o.instance.getClass());
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return this.instance.getClass().getName().compareTo(o.instance.getClass().getName());
    }

    public Map<String, FieldProvider> getFieldMap() {
        if (fieldMap == null) {
            Map<String, Field> fields = FieldUtil.getFields(false, clazz);
            Map<String, FieldProvider> tmpMap = new LinkedHashMap<>();
            Map<Field, FieldProvider> tmpFieldMap = new HashMap<>();
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                FieldProvider fieldProvider = new FieldProvider(this, this.instance, entry.getValue());
                tmpMap.put(entry.getKey(), fieldProvider);
                tmpFieldMap.put(entry.getValue(), fieldProvider);
            }
            fieldMap = tmpMap;
            fieldProviderMap = tmpFieldMap;
        }
        return fieldMap;
    }

    public Map<String, MethodProvider> getMethodMap() {
        if (methodMap == null) {
            Map<String, Method> stringMethodMap = MethodUtil.readAllMethod(false, clazz);
            Map<String, MethodProvider> tmpMap = new LinkedHashMap<>();
            for (Map.Entry<String, Method> entry : stringMethodMap.entrySet()) {
                tmpMap.put(entry.getKey(), new MethodProvider(this.instance, entry.getValue()));
            }
            methodMap = tmpMap;
        }
        return methodMap;
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return AnnUtil.hasAnn(clazz, annotation);
    }

    /** 是否添加了注解 */
    public boolean withSuper(Class<?> cls) {
        return cls.isAssignableFrom(clazz);
    }

    public Stream<MethodProvider> methodStream() {
        return getMethodMap().values().stream();
    }

    /** 所有添加了这个注解的方法 */
    public Stream<MethodProvider> methodsWithAnnotated(Class<? extends Annotation> annotation) {
        return methodStream().filter(provider -> provider.hasAnn(annotation));
    }

    /** 所有添加了这个注解的方法 */
    public Stream<MethodProvider> methodsEqualsParameters(Class<?>... args) {
        return methodStream().filter(provider -> provider.equalsParameters(args));
    }

    /** 所有添加了这个注解的方法 */
    public Stream<MethodProvider> methodsAssignableFrom(Class<?>... args) {
        return methodStream().filter(provider -> provider.isAssignableFrom(args));
    }


    /** 所有的字段 */
    public Stream<FieldProvider> fieldStream() {
        return getFieldMap().values().stream();
    }

    /** 所有添加了这个注解的字段 */
    public Stream<FieldProvider> fieldWithAnnotated(Class<? extends Annotation> annotation) {
        return fieldStream().filter(provider -> provider.hasAnn(annotation));
    }

    @Override public String toString() {
        return "ReflectObjectProvider{instance=%s}".formatted(instance);
    }
}
