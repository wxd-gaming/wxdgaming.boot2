package code.string;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.List;

public class SplitTest {

    @Test
    @RepeatedTest(10)
    public void stringSplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            String[] s = "mongo:key:2:test".split(":");
        }
        System.out.printf("%12s %7.2f ms%n", "stringSplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void diySplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = new StringSplit("mongo:key:2:test", ":").getList();
        }
        System.out.printf("%12s %7.2f ms%n", "diySplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void diySplit2() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = StringSplit.split("mongo:key:2:test", ":");
        }
        System.out.printf("%12s %7.2f ms%n", "diySplit2", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    public void diySplit3() {
        StringSplit stringSplit = new StringSplit("mongo:key:2:test", ":");
        System.out.println(stringSplit.first());
        System.out.println(stringSplit.next());
        System.out.println(stringSplit.last());
        System.out.println(new StringSplit("mongo:key:2:test", ":").getList());
        System.out.println(new StringSplit("mongo:key:2:test", ":").next());
        System.out.println("=======================");

        System.out.println(Arrays.toString("mongo:key:2:test".split("mongo")));
        System.out.println(new StringSplit("mongo:key:2:test", "mongo").getList());
    }


}
