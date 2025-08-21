package code.asm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.LogbackUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.io.Objects;

import java.lang.reflect.Method;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReflectionPerformanceTest {

    static {
        LogbackUtil.refreshLoggerLevel(Level.INFO);
    }


    @Order(2)
    @RepeatedTest(10)
    public void invokeClass() throws Exception {
        SimpleClass obj = new SimpleClass();
        // 反射方法调用
        Method method = SimpleClass.class.getMethod("simpleMethod");
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            method.invoke(obj);
        }
        long endTime = System.nanoTime();
        long proxyTime = endTime - startTime;
        System.out.printf("reflect 调用耗时: %d 纳秒, %s ms%n", proxyTime, proxyTime / 100 / 10000f);
    }

    @Order(3)
    @RepeatedTest(10)
    public void asmProxyClass() throws Exception {
        SimpleClass obj = new SimpleClass();
        // 反射方法调用
        Method method = SimpleClass.class.getMethod("simpleMethod");
        // 代理方法调用
        JavassistProxy javassistProxy = JavassistProxy.of(obj, method);
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            javassistProxy.proxyInvoke(Objects.ZERO_ARRAY);
        }
        long endTime = System.nanoTime();
        long proxyTime = endTime - startTime;
        System.out.printf("asm 调用耗时: %d 纳秒, %s ms%n", proxyTime, proxyTime / 100 / 10000f);
    }


}
