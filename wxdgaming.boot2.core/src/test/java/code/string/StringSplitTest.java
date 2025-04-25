package code.string;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringSplitTest {

    static String delim = ":";

    @Test
    @RepeatedTest(10)
    public void stringSplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            String[] s = "mongo:key:2:test".split(delim);
        }
        System.out.printf("%20s %7.2f ms%n", "String.Split", (System.nanoTime() - start) / 10000 / 100f);
    }

    /** 按照单字符切割最快性能最好 */
    @Test
    @RepeatedTest(10)
    public void stringTokenizer() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            StringTokenizer stringTokenizer = new StringTokenizer("mongo:key:2:test", delim);
            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken();
            }
        }
        System.out.printf("%20s %7.2f ms%n", "stringTokenizer", (System.nanoTime() - start) / 10000 / 100f);
    }

    /** 综合性能最好，最灵活 */
    @Test
    @RepeatedTest(10)
    public void diySplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = new StringSplit("mongo:key:2:test", delim).getList();
        }
        System.out.printf("%20s %7.2f ms%n", "new StringSplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void diySplit2() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = StringSplit.split("mongo:key:2:test", delim);
        }
        System.out.printf("%20s %7.2f ms%n", "static StringSplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void stringSplitIterable() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            StringSplitIterable stringSplitIterable = new StringSplitIterable("mongo:key:2:test", delim);
            while (stringSplitIterable.hasNext()) {
                String s = stringSplitIterable.next();
            }
        }
        System.out.printf("%20s %7.2f ms%n", "stringSplitIterable", (System.nanoTime() - start) / 10000 / 100f);
    }


    @Test
    public void diySplit3() {
        StringSplit stringSplit = new StringSplit("mongo:key:2:test", delim);
        System.out.println(stringSplit.first());
        System.out.println(stringSplit.next());
        System.out.println(stringSplit.last());
        System.out.println(new StringSplit("mongo:key:2:test", delim).getList());
        System.out.println(new StringSplit("mongo:key:2:test", delim).next());
        System.out.println("=======================");

        System.out.println(Arrays.toString("mongo:key:2:test".split("mongo")));
        System.out.println(new StringSplit("mongo:key:2:test", "mongo").getList());
    }


}
