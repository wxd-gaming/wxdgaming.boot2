package code;

import org.junit.Test;
import wxdgaming.boot2.core.timer.CronExpress;

import java.util.concurrent.TimeUnit;

/**
 * ces
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-24 13:23
 **/
public class CronExpressTest {

    @Test
    public void t1() {
        CronExpress cronExpress = new CronExpress("0 0 * * * ?", TimeUnit.MINUTES, 30);
        System.out.println(cronExpress.validateTimeBefore());
        System.out.println(cronExpress.validateTimeAfter());

    }

}
