package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 字段
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-17 10:33
 */
@Slf4j
@Getter
public class ReflectFieldProvider implements Comparable<ReflectFieldProvider> {

    private final Class<?> cls;
    private final Field field;
    private final LazyLoad<Method> setMethodLazy;
    private final LazyLoad<Method> getMethodLazy;

    public ReflectFieldProvider(Class<?> cls, Field field) {
        this.cls = cls;
        this.field = field;
        try {
            this.field.setAccessible(true);
        } catch (Throwable ignore) {}
        this.setMethodLazy = new LazyLoad<>(() -> findSetterMethod(field));
        this.getMethodLazy = new LazyLoad<>(() -> findGetterMethod(field));
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return AnnUtil.hasAnn(field, annotation);
    }

    @Override public final boolean equals(Object o) {
        if (!(o instanceof ReflectFieldProvider that)) return false;

        return Objects.equals(getCls(), that.getCls()) && Objects.equals(getField(), that.getField());
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(getCls());
        result = 31 * result + Objects.hashCode(getField());
        return result;
    }

    @Override public String toString() {
        return "ReflectFieldContext{field=%s}".formatted(field);
    }

    public void setInvoke(Object instance, Object value) {
        try {
            if (setMethodLazy.get() != null) {
                setMethodLazy.get().invoke(instance, value);
            } else {
                field.set(instance, value);
            }
        } catch (Exception e) {
            throw Throw.of(cls.getSimpleName() + "." + field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <R> R getInvoke(Object instance) {
        try {
            if (getMethodLazy.get() != null) {
                return (R) getMethodLazy.get().invoke(instance);
            }
            return (R) field.get(instance);
        } catch (Exception e) {
            throw Throw.of(cls.getSimpleName() + "." + field.getName(), e);
        }
    }

    private Method findSetterMethod(Field field) {
        String fieldName = field.getName();
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method method = cls.getMethod(setterName, field.getType());
            try {
                method.setAccessible(true);
            } catch (Throwable ignore) {}
            return method; // 根据字段类型查找setter
        } catch (NoSuchMethodException e) {
            return null; // 找不到对应setter
        }
    }

    private Method findGetterMethod(Field field) {
        String fieldName = field.getName();
        String getterNamePrefix = "get";
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            getterNamePrefix = "is"; // 如果字段是布尔类型，则使用is前缀
        }
        String getterName = getterNamePrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            Method method = cls.getMethod(getterName);
            try {
                method.setAccessible(true);
            } catch (Throwable ignore) {}
            return method; // 查找无参数的getter方法
        } catch (NoSuchMethodException e) {
            return null; // 找不到对应getter
        }
    }

    @Override public int compareTo(ReflectFieldProvider o) {

        int o1Sort = AnnUtil.orderValue(field, () -> AnnUtil.orderValue(cls));
        int o2Sort = AnnUtil.orderValue(o.field, () -> AnnUtil.orderValue(cls));

        if (o1Sort == o2Sort) {
            /*如果排序值相同，采用名字排序*/
            return field.getName().compareTo(o.field.getName());
        }
        return Integer.compare(o1Sort, o2Sort);
    }

}
