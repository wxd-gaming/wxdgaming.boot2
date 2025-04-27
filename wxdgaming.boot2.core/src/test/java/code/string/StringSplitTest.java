package code.string;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.*;

public class StringSplitTest {

    static final String delim = ":";
    static final String source = new StringJoiner(delim).add("mongo").add("key").add("2").add("test").toString();

    static {
        System.out.println(source);
    }

    @Test
    @RepeatedTest(10)
    public void stringSplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            String[] s = source.split(delim);
        }
        System.out.printf("%24s %7.2f ms%n", "String.Split", (System.nanoTime() - start) / 10000 / 100f);
    }

    /** 按照单字符切割最快性能最好 */
    @Test
    @RepeatedTest(10)
    public void stringTokenizer() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            StringTokenizer stringTokenizer = new StringTokenizer(source, delim);
            while (stringTokenizer.hasMoreTokens()) {
                String string = stringTokenizer.nextToken();
            }
        }
        System.out.printf("%24s %7.2f ms%n", "stringTokenizer", (System.nanoTime() - start) / 10000 / 100f);
    }


    /** 按照单字符切割最快性能最好 */
    @Test
    @RepeatedTest(10)
    public void stringTokenizerList() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> strings = stringTokenizerSplit();
        }
        System.out.printf("%24s %7.2f ms%n", "stringTokenizerList", (System.nanoTime() - start) / 10000 / 100f);
    }


    /** 按照单字符切割最快性能最好 */
    public List<String> stringTokenizerSplit() {
        List<String> list = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(source, delim);
        while (stringTokenizer.hasMoreTokens()) {
            String string = stringTokenizer.nextToken();
            list.add(string);
        }
        return list;
    }

    /** 综合性能最好，最灵活 */
    @Test
    @RepeatedTest(10)
    public void diySplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = new StringSplit(source, delim).getList();
        }
        System.out.printf("%24s %7.2f ms%n", "new StringSplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    public void staticStringSplit() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = StringSplit.split(source, delim);
        }
        System.out.printf("%24s %7.2f ms%n", "static StringSplit", (System.nanoTime() - start) / 10000 / 100f);
    }

    public void staticStringSplit4Char() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = StringSplit4Char.manualSplit(source, delim.charAt(0));
        }
        System.out.printf("%24s %7.2f ms%n", "static StringSplit4Char", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void stringSplit4Char() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            List<String> s = new StringSplit4Char(source, delim).getList();
        }
        System.out.printf("%24s %7.2f ms%n", "new StringSplit4Char", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void stringCharIterable() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            StringSplit4CharIterable stringSplit4CharIterable = new StringSplit4CharIterable(source, delim);
            while (stringSplit4CharIterable.hasNext()) {
                String s = stringSplit4CharIterable.next();
            }
        }
        System.out.printf("%24s %7.2f ms%n", "new CharIterable", (System.nanoTime() - start) / 10000 / 100f);
    }

    @Test
    @RepeatedTest(10)
    public void stringSplitIterable() {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            StringSplitIterable stringSplitIterable = new StringSplitIterable(source, delim);
            while (stringSplitIterable.hasNext()) {
                String s = stringSplitIterable.next();
            }
        }
        System.out.printf("%24s %7.2f ms%n", "stringSplitIterable", (System.nanoTime() - start) / 10000 / 100f);
    }


    @Test
    public void diySplit3() {
        StringSplit stringSplit = new StringSplit(source, delim);
        System.out.println(stringSplit.first());
        System.out.println(stringSplit.next());
        System.out.println(stringSplit.last());
        System.out.println(new StringSplit(source, delim).getList());
        System.out.println(new StringSplit(source, delim).next());
        System.out.println("=======================");

        System.out.println(Arrays.toString(source.split("mongo")));
        System.out.println(new StringSplit(source, "mongo").getList());
    }


}
