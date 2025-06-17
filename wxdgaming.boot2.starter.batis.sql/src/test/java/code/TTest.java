package code;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** 泛型测试 */
public class TTest {

    @Test
    public void t1() throws NoSuchFieldException {
        B b = new B("123");
        System.out.println(b.getT());
        Type genType = B.class.getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Field t = B.class.getSuperclass().getDeclaredField("t");
        Class<?> type = t.getType();
        Type genericType = t.getGenericType();
        System.out.println(type);
        System.out.println(genericType);
    }

    @Getter
    @Setter
    public class A<T> {

        private T t;

        public A(T t) {
            this.t = t;
        }

    }

    public class B extends A<String> {

        public B(String t) {
            super(t);
        }

    }

}
