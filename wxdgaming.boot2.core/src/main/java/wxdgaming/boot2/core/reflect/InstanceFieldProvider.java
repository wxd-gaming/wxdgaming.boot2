package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import wxdgaming.boot2.core.function.FunctionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Getter
public class InstanceFieldProvider implements Comparable<InstanceFieldProvider> {

    private final InstanceProvider instanceProvider;
    private final Object instance;
    private final ReflectFieldProvider fieldProvider;
    private final LazyLoad<InstanceMethodProvider> setterProvider;
    private final LazyLoad<InstanceMethodProvider> getterProvider;

    public InstanceFieldProvider(InstanceProvider instanceProvider, Object instance, ReflectFieldProvider fieldProvider) {
        this.instanceProvider = instanceProvider;
        this.instance = instance;
        this.fieldProvider = fieldProvider;
        this.setterProvider = new LazyLoad<>(() -> {
            Method setterMethod = fieldProvider.getSetMethodLazy().get();
            if (setterMethod == null) return null;
            return new InstanceMethodProvider(instance, setterMethod);
        });
        this.getterProvider = new LazyLoad<>(() -> {
            Method getterMethod = fieldProvider.getGetMethodLazy().get();
            if (getterMethod == null) return null;
            return new InstanceMethodProvider(instance, getterMethod);
        });
    }

    public Object instance() {
        return instance;
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return fieldProvider.hasAnn(annotation);
    }


    public void setInvoke(Object value) {
        if (setterProvider.get() == null) {
            try {
                fieldProvider.setInvoke(instance, value);
            } catch (Exception e) {
                throw FunctionUtil.runtimeException(e);
            }
        } else {
            setterProvider.get().invoke(value);
        }
    }

    public Object getInvoke() {
        if (setterProvider.get() == null) {
            try {
                return fieldProvider.getInvoke(instance);
            } catch (Exception e) {
                throw FunctionUtil.runtimeException(e);
            }
        } else {
            return setterProvider.get().invoke();
        }
    }

    @Override public int compareTo(InstanceFieldProvider o) {
        return this.fieldProvider.compareTo(o.fieldProvider);
    }

    @Override public String toString() {
        return "FieldProvider{instance=%s, field=%s}".formatted(instance, fieldProvider);
    }

}
