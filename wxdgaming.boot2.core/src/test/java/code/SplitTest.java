package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.StackUtils;

import java.util.Arrays;

public class SplitTest {

    public SplitTest() {
        System.out.println(StackUtils.stack());
        System.out.println(StackUtils.stack2());
        System.out.println(StackUtils.stackAll());
        t12();
    }

    @Test
    public void t12() {
        String source = "sdf|wertcwe23";
        System.out.println(Arrays.toString(source.split("[|]")));
        System.out.println(StackUtils.stack());
        System.out.println(StackUtils.stack2());
        System.out.println(StackUtils.stackAll());
    }

}
