package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class DiffTimeRecordTest {

    @Test
    public void d1() {
        StopWatch stopWatch = new StopWatch("测试");
        stopWatch.start("start");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        stopWatch.stop();
        stopWatch.start("sleep");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
        stopWatch.stop();
        stopWatch.start("end");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        stopWatch.stop();
        System.out.println(stopWatch.toString());
        System.out.println(stopWatch.prettyPrint());
    }


    @Test
    public void d2() throws InterruptedException {
        StopWatch stopWatch = new StopWatch("用户处理流程");

        stopWatch.start("查询用户");
        // 查询逻辑...
        Thread.sleep(50);
        stopWatch.stop();

        stopWatch.start("更新缓存");
        // 缓存操作...
        Thread.sleep(80);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }

}
