package code.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.function.StringLazy;

/**
 * s
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-06 20:41
 **/
@Slf4j
public class LogStringTest {

    @Test
    public void t1() {
        int i = 0;
        int y = 2;
        log.info("{}, {}", StringLazy.of(() -> "test"), StringLazy.of(() -> y + "-" + i + "-s-" + "g"));
    }

}
