package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 反射类信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-17 10:22
 **/
@Slf4j
@Getter
public class ReflectClassProvider {

    private final Class<?> clazz;
    private Map<String, Field> fieldMap;
    private final Map<Field, ReflectFieldProvider> fieldProviderMap = new HashMap<>();
    private Map<String, Method> methodMap;

    public ReflectClassProvider(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return clazz.isAssignableFrom(cls);
    }

    @SuppressWarnings("unchecked")
    public <T> T cast(Object obj) {
        return (T) clazz.cast(obj);
    }

    public Map<String, Field> getFieldMap() {
        if (fieldMap == null) {
            this.fieldMap = FieldUtil.getFields(false, clazz);
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
        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameter = parameters[i];
            fullName.append("_").append(parameter.getSimpleName());
        }
        return getMethodMap().get(fullName.toString());
    }

    public ReflectFieldProvider getFieldContext(String fieldName) {
        Field field = getFieldMap().get(fieldName);
        return getFieldContext(field);
    }

    public ReflectFieldProvider getFieldContext(Field field) {
        return fieldProviderMap.computeIfAbsent(field, l -> new ReflectFieldProvider(this, field));
    }

}
