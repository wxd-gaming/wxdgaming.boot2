package code;

import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.ExecutorContext;
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
        ExecutorContext.Content context = ExecutorContext.context();
        context.running("t1");
        c1();
        c2();
        c3();
        c4();
        String release = context.costString();
        System.out.println(release);
    }

    public void c1() {
        ExecutorContext.context().startWatch("c1");
        c2();
        c3();
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ExecutorContext.context().stopWatch();
    }

    public void c2() {
        ExecutorContext.context().startWatch("c2");
        c3();
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ExecutorContext.context().stopWatch();
    }

    public void c3() {
        ExecutorContext.context().startWatch("c3");
        c4();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ExecutorContext.context().stopWatch();
    }

    public void c4() {
        ExecutorContext.context().startWatch("c4");
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(RandomUtils.random(10, 50)));
        ExecutorContext.context().stopWatch();
    }

}
