package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 字段
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-17 10:33
 */
@Slf4j
@Getter
public class ReflectFieldProvider {

    private final ReflectClassProvider reflectClassProvider;
    private final Field field;
    private final Method setMethod;
    private final Method getMethod;

    public ReflectFieldProvider(ReflectClassProvider reflectClassProvider, Field field) {
        this.reflectClassProvider = reflectClassProvider;
        this.field = field;
        this.setMethod = findSetMethod(reflectClassProvider.getClazz(), field);
        this.getMethod = findGetMethod(reflectClassProvider.getClazz(), field);
    }

    @Override public String toString() {
        return "ReflectFieldContext{field=%s, setMethod=%s, getMethod=%s}"
                .formatted(field, setMethod, getMethod);
    }

    public void setInvoke(Object instance, Object value) {
        try {
            if (setMethod != null) {
                setMethod.invoke(instance, value);
            } else {
                field.set(instance, value);
            }
        } catch (Exception e) {
            throw Throw.of(reflectClassProvider.getClazz().getSimpleName() + "." + field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <R> R getInvoke(Object instance) {
        try {
            if (getMethod != null) {
                return (R) getMethod.invoke(instance);
            }
            return (R) field.get(instance);
        } catch (Exception e) {
            throw Throw.of(reflectClassProvider.getClazz().getSimpleName() + "." + field.getName(), e);
        }
    }

    /** 查找get方法 */
    private Method findGetMethod(Class<?> clazz, Field field) {
        Method method = findMethod(clazz, "get", field, 0);
        if (method != null) return method;
        method = findMethod(clazz, "is", field, 0);
        if (method != null) return method;

        if (field.getType().getSimpleName().equalsIgnoreCase(boolean.class.getSimpleName())) {
            method = findMethod(clazz, "", field, 0);
            if (method != null) return method;
        }
        return null;
    }

    /** 查找set方法 */
    private Method findSetMethod(Class<?> clazz, Field field) {
        return findMethod(clazz, "set", field, 1);
    }

    /** 查找方法 */
    private Method findMethod(Class<?> clazz, String prefix, Field field, int parameterCount) {
        return findMethod(clazz, field, prefix, field.getName(), parameterCount);
    }

    private Method findMethod(Class<?> clazz, Field field, String prefix, String fieldName, int parameterCount) {
        for (Method method : reflectClassProvider.getMethodMap().values()) {
            String methodName = method.getName();// 获取每一个方法名
            if (methodName.equalsIgnoreCase(prefix + fieldName)) {
                if (method.getParameterCount() != parameterCount) {
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "类：{} {} method {} 是否有同名方法 当前方法有参数",
                                clazz.getName(), prefix, methodName,
                                new RuntimeException()
                        );
                    }
                    continue;
                }
                if (parameterCount > 0) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    if (!field.getType().equals(parameterType)) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                    "类：{} set method {} 是否有同名方法 当前参数类型不一致 字段参数类型：{}, set 方法参数类型：{}",
                                    clazz.getName(), methodName, field.getType(), parameterType,
                                    new RuntimeException()
                            );
                        }
                        continue;
                    }
                }
                method.setAccessible(true);
                return method;
            }
        }
        if (boolean.class.getSimpleName().equalsIgnoreCase(field.getType().getSimpleName().toLowerCase())) {
            if (fieldName.startsWith("is")) {
                return findMethod(clazz, field, prefix, fieldName.substring(2), parameterCount);
            }
        }
        return null;
    }

}
