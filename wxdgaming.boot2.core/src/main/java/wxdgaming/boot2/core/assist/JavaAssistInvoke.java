package wxdgaming.boot2.core.assist;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * assist asm 的代理类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-03 17:26
 **/
@Slf4j
@Getter
public class JavaAssistInvoke {

    /** 创建代理对象 需要代理的方法因为反射和泛型类型擦除问题，不要使用，。比如int long 之类的值类型。改为integer */
    public static JavaAssistInvoke of(Object invokeInstance, Method method) {
        Class<?> invokeClass = invokeInstance.getClass();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderArgs = new StringBuilder();
        stringBuilder.append("public Object invoke(Object[] args) {\n");
        Integer integer = 1;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];

            if (parameterType == int.class
                || parameterType == long.class
                || parameterType == double.class
                || parameterType == float.class
                || parameterType == short.class
                || parameterType == byte.class
                || parameterType == boolean.class) {
                throw new RuntimeException("%s.%s 请勿使用值类型 ，比如int 改为 integer, long 改为 Long 等".formatted(invokeInstance.getClass(),method.getName()));
            }

            if (!stringBuilderArgs.isEmpty()) stringBuilderArgs.append(", ");
            stringBuilderArgs.append("var").append(i);

            stringBuilder.append("    ").append(parameterType.getName()).append(" ").append("var").append(i).append(" = ")
                    .append("(").append(parameterType.getName()).append(")args[").append(i).append("]").append(";").append("\n");
        }
        stringBuilder.append("    ").append(invokeClass.getName()).append(" proxy = ").append("(").append(invokeClass.getName()).append(")instance;").append("\n");
        stringBuilder.append("    ").append("Object result = ").append("null").append(";").append("\n");
        if (method.getReturnType() != void.class) {
            stringBuilder.append("    ").append("result = proxy.").append(method.getName()).append("(").append(stringBuilderArgs).append(");").append("\n");
        } else {
            stringBuilder.append("    ").append("proxy.").append(method.getName()).append("(").append(stringBuilderArgs).append(");").append("\n");
        }
        stringBuilder.append("    ").append("return result;").append("\n");
        stringBuilder.append("}");
        String methodBody = stringBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug("\n{}", methodBody);
        }
        JavaAssistBox.JavaAssist javaAssist = JavaAssistBox.DefaultJavaAssistBox.extendSuperclass(JavaAssistInvoke.class, invokeClass.getClassLoader());
        javaAssist.createMethod(methodBody);
        if (log.isDebugEnabled()) {
            javaAssist.writeFile("target/bin");
        }
        JavaAssistInvoke javaAssistInvoke = javaAssist.toInstance();
        javaAssistInvoke.init(invokeInstance, method);
        return javaAssistInvoke;
    }


    protected Object instance;
    protected Method method;

    public void init(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object invoke(Object[] args) {
        throw new RuntimeException("not implement");
    }

}
