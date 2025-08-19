package code;

import org.junit.Test;

import java.util.function.Function;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 11:02
 **/
public class FunctionTest {

    @Test
    public void f1() {
        Function<String, String> trim = String::trim;
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> validate = s -> {
            if (s.length() > 20) throw new IllegalArgumentException("过长");
            return s;
        };

        Function<String, String> composed = trim.andThen(upper).andThen(validate);
        System.out.println(composed.apply("   abcd 3 efg   "));
        System.out.println(composed.apply("   abcd 4 efg   "));
    }

}
