package wxdgaming.boot2.core.assist;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * assist asm 的代理类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-03 17:26
 **/
@Slf4j
@Getter
public class JavassistProxy {

    /** 创建代理对象 */
    public static JavassistProxy of(Object invokeInstance, Method method) {
        Class<?> invokeClass = invokeInstance.getClass();
        StringBuilder stringBuilderArgs = new StringBuilder();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];

            if (!stringBuilderArgs.isEmpty()) stringBuilderArgs.append(",\n");

            if (parameterType == int.class) {
                stringBuilderArgs.append("((Integer)args[%d]).intValue()".formatted(i));
            } else if (parameterType == long.class) {
                stringBuilderArgs.append("((Long)args[%d]).longValue()".formatted(i));
            } else if (parameterType == double.class) {
                stringBuilderArgs.append("((Double)args[%d]).doubleValue()".formatted(i));
            } else if (parameterType == float.class) {
                stringBuilderArgs.append("((Float)args[%d]).floatValue()".formatted(i));
            } else if (parameterType == short.class) {
                stringBuilderArgs.append("((Short)args[%d]).shortValue()".formatted(i));
            } else if (parameterType == byte.class) {
                stringBuilderArgs.append("((Byte)args[%d]).byteValue()".formatted(i));
            } else if (parameterType == boolean.class) {
                stringBuilderArgs.append("((Boolean)args[%d]).booleanValue()".formatted(i));
            } else {
                stringBuilderArgs.append("(%s)args[%d]".formatted(parameterType.getName(), i));
            }
        }
        String methodBody = null;
        if (method.getReturnType() != void.class) {
            methodBody = JavassistProxy.buildReturn(invokeClass.getName(), method.getName(), stringBuilderArgs.toString());
        } else {
            methodBody = JavassistProxy.buildVoid(invokeClass.getName(), method.getName(), stringBuilderArgs.toString());
        }
        if (log.isTraceEnabled()) {
            log.trace("\n{}", methodBody);
        }
        JavassistBox.JavaAssist javaAssist = JavassistBox.defaultJavassistBox.extendSuperclass(
                JavassistProxy.class,
                invokeClass.getClassLoader(),
                invokeClass.getName() + "$" + method.getName()
        );
        javaAssist.createMethod(methodBody);
        if (log.isDebugEnabled()) {
            javaAssist.writeFile("target/bin");
        }

        JavassistProxy javassistProxy = javaAssist.toInstance();
        javassistProxy.init(invokeInstance, method);
        javaAssist.getCtClass().defrost();
        javaAssist.getCtClass().detach();
        return javassistProxy;
    }


    private static String buildVoid(String className, String methodName, String parameter) {
        return """
                
                public Object proxyInvoke(Object[] args) {
                    %s proxy = (%s)instance;
                    proxy.%s(
                    %s
                    );
                    return null;
                }
                
                """.formatted(className, className, methodName, parameter);
    }

    private static String buildReturn(String className, String methodName, String parameter) {
        return """
                
                public Object proxyInvoke(Object[] args) {
                    %s proxy = (%s)instance;
                    return proxy.%s(
                    %s
                    );
                }
                
                """.formatted(className, className, methodName, parameter);
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
