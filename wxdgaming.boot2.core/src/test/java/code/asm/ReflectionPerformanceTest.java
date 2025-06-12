package code.asm;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.LogbackUtil;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import wxdgaming.boot2.core.assist.Javassist2Proxy;
import wxdgaming.boot2.core.assist.JavassistProxy;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.loader.ClassDirLoader;
import wxdgaming.boot2.core.reflect.ReflectContext;

import java.lang.reflect.Method;


public class ReflectionPerformanceTest {

    static {
        LogbackUtil.refreshLoggerLevel(Level.INFO);
    }

    @Test
    @RepeatedTest(10)
    public void tempProxyClass() throws Exception {
        SimpleClass obj = new SimpleClass();
        // 反射方法调用
        Method method = SimpleClass.class.getMethod("simpleMethod");
        // 直接方法调用
        TempProxyClass proxyClass = new TempProxyClass();
        proxyClass.init(obj, method);
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            proxyClass.proxy0(Objects.ZERO_ARRAY);
        }
        long endTime = System.nanoTime();
        long proxyTime = endTime - startTime;
        System.out.printf("直接调用 调用耗时: %d 纳秒, %s ms%n", proxyTime, proxyTime / 100 / 10000f);
    }

    @Test
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

    @Test
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

    @Test
    @RepeatedTest(10)
    public void compilerCodeClass() throws Exception {
        SimpleClass obj = new SimpleClass();
        // 反射方法调用
        Method method = SimpleClass.class.getMethod("simpleMethod");
        // 代理方法调用
        Javassist2Proxy javassistProxy = Javassist2Proxy.of(obj, method);
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            javassistProxy.proxyInvoke(Objects.ZERO_ARRAY);
        }
        long endTime = System.nanoTime();
        long proxyTime = endTime - startTime;
        System.out.printf("compiler 调用耗时: %d 纳秒, %s ms%n", proxyTime, proxyTime / 100 / 10000f);
    }

    @Test
    @RepeatedTest(10)
    public void compilerCode2Class() throws Exception {
        ClassDirLoader classDirLoader = new ClassDirLoader("target/test-classes");
        Class<?> aClass = classDirLoader.findClass("code.asm.SimpleClass");
        Object object = ReflectContext.newInstance(aClass);
        // 反射方法调用
        Method method = aClass.getMethod("simpleMethod");
        // 代理方法调用
        Javassist2Proxy javassistProxy = Javassist2Proxy.of(object, method);
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            javassistProxy.proxyInvoke(Objects.ZERO_ARRAY);
        }
        long endTime = System.nanoTime();
        long proxyTime = endTime - startTime;
        System.out.printf("compiler 调用耗时: %d 纳秒, %s ms%n", proxyTime, proxyTime / 100 / 10000f);
    }


}
