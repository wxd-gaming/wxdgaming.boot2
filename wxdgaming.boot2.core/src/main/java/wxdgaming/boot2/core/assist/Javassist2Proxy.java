package wxdgaming.boot2.core.assist;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.loader.JavaCoderCompile;
import wxdgaming.boot2.core.reflect.ReflectContext;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * assist asm 的代理类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-03 17:26
 **/
@Slf4j
@Getter
public class Javassist2Proxy {

    static AtomicLong implementationId = new AtomicLong(0);

    /** 创建代理对象 */
    public static Javassist2Proxy of(Object invokeInstance, Method method) {
        Class<?> invokeClass = invokeInstance.getClass();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderArgs = new StringBuilder();
        stringBuilder.append("package ").append(invokeClass.getPackageName()).append(";\n");
        stringBuilder.append("import ").append(Javassist2Proxy.class.getName()).append(";\n");

        final long incrementAndGet = implementationId.incrementAndGet();

        stringBuilder.append("public class Javassist2ProxyImpl%s extends %s { \n".formatted(incrementAndGet, Javassist2Proxy.class.getName()));

        stringBuilder.append("public Object proxyInvoke(Object[] args) {\n");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];

            if (!stringBuilderArgs.isEmpty()) stringBuilderArgs.append(",\n");

            if (parameterType == int.class) {
                stringBuilderArgs.append("((Integer)args[").append(i).append("]).intValue()");
            } else if (parameterType == long.class) {
                stringBuilderArgs.append("((Long)args[").append(i).append("]).longValue()");
            } else if (parameterType == double.class) {
                stringBuilderArgs.append("((Double)args[").append(i).append("]).doubleValue()");
            } else if (parameterType == float.class) {
                stringBuilderArgs.append("((Float)args[").append(i).append("]).floatValue()");
            } else if (parameterType == short.class) {
                stringBuilderArgs.append("((Short)args[").append(i).append("]).shortValue()");
            } else if (parameterType == byte.class) {
                stringBuilderArgs.append("((Byte)args[").append(i).append("]).byteValue()");
            } else if (parameterType == boolean.class) {
                stringBuilderArgs.append("((Boolean)args[").append(i).append("]).booleanValue()");
            } else {
                stringBuilderArgs.append("(" + parameterType.getName() + ")args[").append(i).append("]");
            }
        }

        String invokeClassName = invokeClass.getName();

        invokeClassName = invokeClassName.replace("$", ".");

        stringBuilder.append("    ").append(invokeClassName).append(" proxy = ").append("(").append(invokeClassName).append(")instance;").append("\n");
        stringBuilder.append("    ").append("Object result = ").append("null").append(";").append("\n");
        if (method.getReturnType() != void.class) {
            stringBuilder.append("    ").append("result = proxy.").append(method.getName()).append("(").append(stringBuilderArgs).append(");").append("\n");
        } else {
            stringBuilder.append("    ").append("proxy.").append(method.getName()).append("(\n").append(stringBuilderArgs).append("\n);").append("\n");
        }
        stringBuilder.append("    ").append("return result;").append("\n");
        stringBuilder.append("}\n");
        stringBuilder.append("}");
        String javaCoder = stringBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug("\n{}", javaCoder);
        }

        String className = invokeClass.getPackageName() + ".Javassist2ProxyImpl" + incrementAndGet;

        try {
            Class<?> javassistProxy = new JavaCoderCompile()
                    .parentClassLoader(invokeClass.getClassLoader())
                    .compilerCode(javaCoder)
                    .classLoader()
                    .loadClass(className);
            Javassist2Proxy javassist2Proxy = (Javassist2Proxy) ReflectContext.newInstance(javassistProxy);
            javassist2Proxy.init(invokeInstance, method);
            return javassist2Proxy;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    protected Object instance;
    protected Method method;

    public void init(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object proxyInvoke(Object[] args) {
        throw new RuntimeException("not implement");
    }

}
