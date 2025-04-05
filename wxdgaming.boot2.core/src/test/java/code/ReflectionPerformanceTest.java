package code;

import java.lang.reflect.Method;

// 定义一个简单的类
class SimpleClass {
    public void simpleMethod() {
        // 简单的空方法
    }
}

public class ReflectionPerformanceTest {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            proxy();
            System.out.println("=======================================");
        }
    }

    public static void proxy() throws Exception {
        SimpleClass obj = new SimpleClass();

        // 直接方法调用
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            obj.simpleMethod();
        }
        long endTime = System.nanoTime();
        long directTime = endTime - startTime;
        System.out.println("直接方法调用耗时: " + directTime + " 纳秒");

        // 反射方法调用
        Method method = SimpleClass.class.getMethod("simpleMethod");
        startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            method.invoke(obj);
        }
        endTime = System.nanoTime();
        long reflectionTime = endTime - startTime;
        System.out.println("反射方法调用耗时: " + reflectionTime + " 纳秒");

        System.out.println("反射调用比直接调用慢 " + (reflectionTime - directTime) + " 纳秒");
        System.out.println("反射调用比直接调用慢 " + (reflectionTime - directTime) / 100_0000 + " 毫秒");
    }
}
