package code.asm;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.assist.JavassistBox;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.reflect.MethodUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AsmTest {


    @Test
    public void a1() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login");
        JavassistProxy javassistProxy = JavassistProxy.of(new LoginHandler(), method);
        javassistProxy.proxyInvoke(new Object[]{1, "123456"});
    }

    @Test
    public void a2() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login2");
        JavassistProxy javassistProxy = JavassistProxy.of(new LoginHandler(), method);
        javassistProxy.proxyInvoke(new Object[]{true, (byte) 1, 1, 1, "123456"});
    }

    @Test
    public void a3() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;


        JavassistBox.JavaAssist javaAssist = JavassistBox.of().editClass(loginHandlerClass);
        javaAssist.declaredMethod("login", new Class[]{Integer.class, String.class}, ctmethod -> {
            ctmethod.insertBefore("System.out.println(\"insertBefore\");");
            ctmethod.insertAfter("System.out.println(\"insertAfter\");");
            ctmethod.insertAfter("System.out.println(\"insertAfter2\");", true);
        });
        javaAssist.writeFile("target/out");
        Object instance = javaAssist.toInstance();
        Method method = MethodUtil.findMethod(false, instance.getClass(), "login", new Class[]{Integer.class, String.class});
        JavassistProxy javassistProxy = JavassistProxy.of(instance, method);
        javassistProxy.proxyInvoke(new Object[]{1, "123456"});

    }

}
