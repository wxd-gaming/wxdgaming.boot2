package code.asm;

import org.junit.Test;
import wxdgaming.boot2.core.assist.JavaAssistInvoke;
import wxdgaming.boot2.core.reflect.MethodUtil;

import java.lang.reflect.Method;

public class AsmTest {


    @Test
    public void a1() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login");
        JavaAssistInvoke javaAssistInvoke = JavaAssistInvoke.of(new LoginHandler(), method);
        javaAssistInvoke.invoke(new Object[]{1, "123456"});
    }

}
