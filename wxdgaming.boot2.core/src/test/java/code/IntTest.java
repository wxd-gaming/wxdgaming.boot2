package code;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 13:34
 **/
public class IntTest {

    @Test
    public void i1() {
        int i = 0;
        System.out.println(i++);
        System.out.println(++i);
    }

    @Test
    public void show() {
        Object t = new Object[]{1, 2};
        out(t);
        out((Object[]) t);
        out(1, 2);
    }

    public void out(Object... args) {
        System.out.println(Arrays.toString(args));
    }

}

