package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.timer.MyClock;

import java.time.Instant;

public class MyClockTest {

    @Test
    public void m1() {
        System.out.println(MyClock.getQuarterOfYear());
        System.out.println(MyClock.getDateMonthDayString());
        System.out.println(MyClock.getDateYearDayString());

        Instant now = Instant.now();

    }

}
