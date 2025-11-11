package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.function.FunctionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class MethodProvider implements Comparable<MethodProvider> {

    private final Object instance;
    private final Method method;
    private final AtomicInteger invokeCount = new AtomicInteger(0);
    private JavassistProxy javassistProxy = null;

    public MethodProvider(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        try {
            method.setAccessible(true);
        } catch (Throwable ignore) {}
    }

    public Object instance() {
        return instance;
    }

    public Object invoke(Object... args) {
        if (invokeCount.get() >= 10) {
            if (javassistProxy == null) {
                javassistProxy = JavassistProxy.of(instance, method);
            }
            return javassistProxy.proxyInvoke(args);
        } else {
            try {
                return method.invoke(instance, args);
            } catch (Exception e) {
                throw FunctionUtil.runtimeException(e);
            }
        }
    }

    @Override public int compareTo(MethodProvider o) {
        int o1Sort = AnnUtil.orderValue(method, () -> AnnUtil.orderValue(instance().getClass()));
        int o2Sort = AnnUtil.orderValue(o.method, () -> AnnUtil.orderValue(o.instance().getClass()));

        if (o1Sort == o2Sort) {
            /*如果排序值相同，采用名字排序*/
            return method.getName().compareTo(o.method.getName());
        }
        return Integer.compare(o1Sort, o2Sort);
    }

    /** 是否添加了注解 */
    public boolean hasAnn(Class<? extends Annotation> annotation) {
        return AnnUtil.hasAnn(method, annotation);
    }

    /** 参数类型强制匹配关系 */
    public boolean equalsParameters(Class<?>... args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes.length == args.length && Arrays.equals(parameterTypes, args);
    }

    /** 参数类型继承关系 */
    public boolean isAssignableFrom(Class<?>... args) {
        return MethodUtil.isAssignableFrom(method, args);
    }

    @Override public String toString() {
        return "MethodProvider{instance=%s, method=%s}".formatted(instance, method);
    }
}
