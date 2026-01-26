package executor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class HeartDriveHandlerTest implements HeartDriveHandler {

    @Test
    public void t1() {
        AbstractExecutorService executorServicePlatform = new ExecutorServicePlatform("platform", 4, 1000, QueuePolicyConst.AbortPolicy);
        HeartDriveRunnable heartDriveRunnable = new HeartDriveRunnable(executorServicePlatform, "heart", 30, TimeUnit.MILLISECONDS);
        heartDriveRunnable.setDriveHandler(this);
        LockSupport.parkNanos(TimeUnit.MINUTES.toNanos(3));
    }

    @Override public void heart(long millis) {
        log.info("!");
    }

    @Override public void heartSecond(int second) {
        log.info("!");
    }

    @Override public void heartMinute(int minute) {
        log.info("!");
    }

    @Override public void heartHour(int hour) {
        log.info("!");
    }

    @Override public void heartDayEnd(int dayOfYear) {
        log.info("!");
    }

    @Override public void heartWeek(long weekFirstDayStartTime) {
        log.info("!");
    }
}
