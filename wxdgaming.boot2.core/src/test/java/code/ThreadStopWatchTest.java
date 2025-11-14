package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.ThreadStopWatch;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-11-14 09:05
 **/
public class ThreadStopWatchTest {

    @Test
    public void t1() {
        ThreadStopWatch.init(TimeUnit.MICROSECONDS, "t1");
        c1();
        c2();
        c3();
        c4();
        String release = ThreadStopWatch.releasePrint();
        System.out.println(release);
    }

    public void c1() {
        ThreadStopWatch.start("c1");
        c2();
        c3();
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ThreadStopWatch.stop();
    }

    public void c2() {
        ThreadStopWatch.start("c2");
        c3();
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ThreadStopWatch.stop();
    }

    public void c3() {
        ThreadStopWatch.start("c3");
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ThreadStopWatch.stop();
    }

    public void c4() {
        ThreadStopWatch.start("c4");
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ThreadStopWatch.stop();
    }

}
