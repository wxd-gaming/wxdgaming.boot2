package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import wxdgaming.boot2.core.function.FunctionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter
public class FieldProvider implements Comparable<FieldProvider> {

    private final ReflectObjectProvider reflectObjectProvider;
    private final Object instance;
    private final Field field;
    private final LazyLoad<MethodProvider> setterProvider;
    private final LazyLoad<MethodProvider> getterProvider;

    public FieldProvider(ReflectObjectProvider reflectObjectProvider, Object instance, Field field) {
        this.reflectObjectProvider = reflectObjectProvider;
        this.instance = instance;
        this.field = field;
        try {
            this.field.setAccessible(true);
        } catch (Throwable ignore) {}
        this.setterProvider = new LazyLoad<>(() -> {
            Method setterMethod = findSetterMethod(field);
            if (setterMethod == null) return null;
            return new MethodProvider(instance, setterMethod);
        });
        this.getterProvider = new LazyLoad<>(() -> {
            Method getterMethod = findGetterMethod(field);
            if (getterMethod == null) return null;
            return new MethodProvider(instance, getterMethod);
        });
    }

    public Object instance() {
        return instance;
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return AnnUtil.hasAnn(field, annotation);
    }

    // 假设的findSetterMethod示例实现，请根据实际情况调整
    private Method findSetterMethod(Field field) {
        String fieldName = field.getName();
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            return reflectObjectProvider.getClazz().getMethod(setterName, field.getType()); // 根据字段类型查找setter
        } catch (NoSuchMethodException e) {
            return null; // 找不到对应setter
        }
    }    // 假设的findGetterMethod示例实现，请根据实际情况调整

    // 修改后的findGetterMethod示例实现，请根据实际情况调整
    private Method findGetterMethod(Field field) {
        String fieldName = field.getName();
        String getterNamePrefix = "get";
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            getterNamePrefix = "is"; // 如果字段是布尔类型，则使用is前缀
        }
        String getterName = getterNamePrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        try {
            return reflectObjectProvider.getClazz().getMethod(getterName); // 查找无参数的getter方法
        } catch (NoSuchMethodException e) {
            return null; // 找不到对应getter
        }
    }

    public void setInvoke(Object value) {
        if (setterProvider.get() == null) {
            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw FunctionUtil.runtimeException(e);
            }
        } else {
            setterProvider.get().invoke(value);
        }
    }

    public Object getInvoke() {
        if (setterProvider.get() == null) {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                throw FunctionUtil.runtimeException(e);
            }
        } else {
            return setterProvider.get().invoke();
        }
    }

    @Override public int compareTo(FieldProvider o) {

        int o1Sort = AnnUtil.orderValue(field, () -> AnnUtil.orderValue(instance().getClass()));
        int o2Sort = AnnUtil.orderValue(o.field, () -> AnnUtil.orderValue(o.instance().getClass()));

        if (o1Sort == o2Sort) {
            /*如果排序值相同，采用名字排序*/
            return field.getName().compareTo(o.getField().getName());
        }
        return Integer.compare(o1Sort, o2Sort);
    }

    @Override public String toString() {
        return "FieldProvider{instance=%s, field=%s}".formatted(instance, field);
    }

}
