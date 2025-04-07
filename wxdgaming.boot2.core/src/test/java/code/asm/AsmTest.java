package code.asm;

import org.junit.Test;
import wxdgaming.boot2.core.assist.JavassistInvoke;
import wxdgaming.boot2.core.reflect.MethodUtil;

import java.lang.reflect.Method;

public class AsmTest {


    @Test
    public void a1() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login");
        JavassistInvoke javassistInvoke = JavassistInvoke.of(new LoginHandler(), method);
        javassistInvoke.invoke(new Object[]{1, "123456"});
    }

    @Test
    public void a2() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login2");
        JavassistInvoke javassistInvoke = JavassistInvoke.of(new LoginHandler(), method);
        javassistInvoke.invoke(new Object[]{true, (byte) 1, 1, 1, "123456"});
    }

}
