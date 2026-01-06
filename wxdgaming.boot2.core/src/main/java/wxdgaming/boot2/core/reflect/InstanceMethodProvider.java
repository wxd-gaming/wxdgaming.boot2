package wxdgaming.boot2.core.reflect;

import lombok.Getter;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.assist.JavassistProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class InstanceMethodProvider implements Comparable<InstanceMethodProvider> {

    private final Object instance;
    private final Method method;
    private final AtomicInteger invokeCount = new AtomicInteger(0);
    private JavassistProxy javassistProxy = null;

    public InstanceMethodProvider(Object instance, Method method) {
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
        try {
            /* TODO spring aop 开启之后字节码增强会失效，无法创建字节码代理 */
            /* if (invokeCount.get() >= 10) {
                if (javassistProxy == null) {
                    javassistProxy = JavassistProxy.of(instance, method);
                }
                return javassistProxy.proxyInvoke(args);
            } else */
            {
                invokeCount.incrementAndGet();
                return method.invoke(instance, args);
            }
        } catch (Exception e) {
            String msg = """
                    
                     class: %s
                    method: %s
                      args: %s
                    
                    """.formatted(instance.getClass().getName(), method.getName(), Arrays.toString(args));
            if (e instanceof InvocationTargetException invocationTargetException) {
                Throwable cause = invocationTargetException.getCause();
                throw Throw.of(msg, cause);
            } else {
                throw Throw.of(msg, e);
            }
        }
    }

    @Override public int compareTo(InstanceMethodProvider o) {
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
        return "MethodProvider{instance=%s, method=%s}".formatted(instance.getClass(), method);
    }
}
