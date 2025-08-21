package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.format.data.Data2Size;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SizeTest {


    @Test
    public void s1() {

        System.out.println(Data2Size.totalSizes0(new A1()));
        System.out.println(Data2Size.totalSizes0(new A2()));
        System.out.println(Data2Size.totalSizes0(new A3()));
        System.out.println(Data2Size.totalSizes0(new A4()));
        System.out.println(Data2Size.totalSizes0(new A5()));

        System.out.println(Data2Size.totalSizes0(new ArrayList<>()));
        System.out.println(Data2Size.totalSizes0(new ArrayList<>(4)));

        int r = 512;
        int f = r * r;

        List<PointInt> clsInts = new ArrayList<>();
        for (int i = 0; i < f; i++) {
            clsInts.add(new PointInt());
        }

        List<PointShort> clsShorts = new ArrayList<>();
        for (int i = 0; i < f; i++) {
            clsShorts.add(new PointShort());
        }


        System.out.println(Data2Size.totalSizes0(clsInts));
        System.out.println(Data2Size.totalSizes0(clsShorts));

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

    static class PointInt {
        int x = 3;
        int y = 4;
        ArrayList<Integer> list = new ArrayList<>(16);
    }

    static class PointShort {
        short x = 3;
        short y = 4;
        ArrayList<Integer> list = new ArrayList<>(16);
    }

}
