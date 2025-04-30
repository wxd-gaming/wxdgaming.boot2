package code;

import org.junit.Test;
import wxdgaming.boot2.core.format.data.Data2Size;

import java.util.concurrent.atomic.AtomicBoolean;

public class SizeTest {


    @Test
    public void s1() {

        System.out.println(Data2Size.totalSizes0(new A1()));
        System.out.println(Data2Size.totalSizes0(new A2()));
        System.out.println(Data2Size.totalSizes0(new A3()));
        System.out.println(Data2Size.totalSizes0(new A4()));
        System.out.println(Data2Size.totalSizes0(new A5()));

    }

    class A1 {
        volatile boolean b10 = false;
    }

    class A2 {
        volatile Boolean b10 = null;
    }

    class A3 {
        volatile Boolean b10 = Boolean.FALSE;
    }

    class A4 {
        AtomicBoolean b10 = null;
    }

    class A5 {
        AtomicBoolean b10 = new AtomicBoolean(false);
    }

}
