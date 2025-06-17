package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射类信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-17 10:22
 **/
@Slf4j
@Getter
public class ReflectClassContext {

    private final String className;
    private final String simpleName;
    private final Class<?> clazz;
    private Map<String, Field> fieldMap;
    private final Map<String, ReflectFieldContext> fieldContextMap = new HashMap<>();
    private Map<String, Method> methodMap;

    public ReflectClassContext(Class<?> clazz) {
        this.className = clazz.getName();
        this.simpleName = clazz.getSimpleName();
        this.clazz = clazz;
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

    public Method findMethod(String methodName, Class<?>... parameters) {
        StringBuilder fullName = new StringBuilder(methodName);
        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameter = parameters[i];
            fullName.append("_").append(parameter.getSimpleName());
        }
        return getMethodMap().get(fullName.toString());
    }

    public ReflectFieldContext getFieldContext(String fieldName) {
        return fieldContextMap.computeIfAbsent(fieldName, l -> {
            Field field = getFieldMap().get(fieldName);
            return new ReflectFieldContext(this, field);
        });
    }

}
