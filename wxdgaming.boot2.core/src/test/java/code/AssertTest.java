package code;

import com.google.common.util.concurrent.Monitor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class AssertTest {


    @Test
    public void a() {
        String format = "dddd";
        System.out.println(format.formatted(1));
    }

    @Test
    public void t1() {
        IllegalArgumentException assertException = new IllegalArgumentException("1");
        log.info("d", assertException);
    }
}
