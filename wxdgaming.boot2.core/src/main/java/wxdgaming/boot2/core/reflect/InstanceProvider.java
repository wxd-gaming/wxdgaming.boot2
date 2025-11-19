package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.proxy.AopProxyUtil;

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
public class InstanceProvider implements Comparable<InstanceProvider> {

    private final Object instance;
    private final ReflectClassProvider reflectClassProvider;
    private Map<String, InstanceFieldProvider> fieldMap;
    private Map<Field, InstanceFieldProvider> fieldProviderMap;
    private Map<String, InstanceMethodProvider> methodMap;

    public InstanceProvider(Object instance) {
        this.instance = instance;
        Object targetObject = AopProxyUtil.getTargetObject(instance);
        this.reflectClassProvider = ReflectClassProvider.build(targetObject.getClass());
    }

    @Override public int compareTo(InstanceProvider o) {
        int o1Annotation = AnnUtil.orderValue(this.instance.getClass());
        int o2Annotation = AnnUtil.orderValue(o.instance.getClass());
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return this.instance.getClass().getName().compareTo(o.instance.getClass().getName());
    }

    public Map<String, InstanceFieldProvider> getFieldMap() {
        if (fieldMap == null) {
            Map<String, ReflectFieldProvider> fields = reflectClassProvider.getFieldMap();
            Map<String, InstanceFieldProvider> tmpMap = new LinkedHashMap<>();
            Map<Field, InstanceFieldProvider> tmpFieldMap = new HashMap<>();
            for (Map.Entry<String, ReflectFieldProvider> entry : fields.entrySet()) {
                InstanceFieldProvider instanceFieldProvider = new InstanceFieldProvider(this, this.instance, entry.getValue());
                tmpMap.put(entry.getKey(), instanceFieldProvider);
                tmpFieldMap.put(entry.getValue().getField(), instanceFieldProvider);
            }
            fieldMap = tmpMap;
            fieldProviderMap = tmpFieldMap;
        }
        return fieldMap;
    }

    public Map<String, InstanceMethodProvider> getMethodMap() {
        if (methodMap == null) {
            Map<String, Method> methodMaps = reflectClassProvider.getMethodMap();
            Map<String, InstanceMethodProvider> tmpMap = new LinkedHashMap<>();
            for (Map.Entry<String, Method> entry : methodMaps.entrySet()) {
                tmpMap.put(entry.getKey(), new InstanceMethodProvider(this.instance, entry.getValue()));
            }
            methodMap = tmpMap;
        }
        return methodMap;
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return reflectClassProvider.isAssignableFrom(cls);
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return reflectClassProvider.hasAnn(annotation);
    }

    /** 是否添加了注解 */
    public boolean withSuper(Class<?> cls) {
        return cls.isAssignableFrom(reflectClassProvider.getClazz());
    }

    public Stream<InstanceMethodProvider> methodStream() {
        return getMethodMap().values().stream();
    }

    /** 所有添加了这个注解的方法 */
    public Stream<InstanceMethodProvider> methodsWithAnnotated(Class<? extends Annotation> annotation) {
        return methodStream().filter(provider -> provider.hasAnn(annotation));
    }

    /** 所有添加了这个注解的方法 */
    public Stream<InstanceMethodProvider> methodsEqualsParameters(Class<?>... args) {
        return methodStream().filter(provider -> provider.equalsParameters(args));
    }

    /** 所有添加了这个注解的方法 */
    public Stream<InstanceMethodProvider> methodsAssignableFrom(Class<?>... args) {
        return methodStream().filter(provider -> provider.isAssignableFrom(args));
    }


    /** 所有的字段 */
    public Stream<InstanceFieldProvider> fieldStream() {
        return getFieldMap().values().stream();
    }

    /** 所有添加了这个注解的字段 */
    public Stream<InstanceFieldProvider> fieldWithAnnotated(Class<? extends Annotation> annotation) {
        return fieldStream().filter(provider -> provider.hasAnn(annotation));
    }

    @Override public String toString() {
        return "ReflectObjectProvider{instance=%s}".formatted(instance);
    }
}
