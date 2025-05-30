package code.number;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class NumberUtilTest {


    @Test
    public void t1() {
        Float.parseFloat("123.");
        Float.parseFloat(".123");
    }

}
