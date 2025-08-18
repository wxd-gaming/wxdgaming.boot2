package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.concurrent.TimeUnit;

/**
 * 测试log输出格式
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-13 11:46
 **/
@Slf4j
public class LogJsonTest {

    @Test
    public void t1() {
        log.info("{}", "1");
        MyClock.TimeOffset.set(TimeUnit.HOURS.toMillis(3));
        log.error("{}", "1", new RuntimeException("1"));
    }

}
