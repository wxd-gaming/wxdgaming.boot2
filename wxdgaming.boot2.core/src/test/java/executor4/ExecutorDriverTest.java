package executor4;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * ExecutorDriver 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 20:01
 **/
@Slf4j
public class ExecutorDriverTest {

    ExecutorDriverService executorFactory;

    @BeforeEach
    public void startUp() {
        executorFactory = new ExecutorDriverService("test", 4);
        for (int i = 0; i < 10; i++) {
            executorFactory.driver("d" + (i + 1));
        }
    }

    @Test
    public void t1() {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorFactory.driver("d" + (i % 10)).execute(() -> {
                System.out.println(ExecutorFactory.currentExecutorName() + " - " + finalI);
            });
        }
    }

    @Test
    public void t2() {
        log.info("start");
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorFactory.driver("d" + (i % 10)).scheduleWithFixedDelay(() -> {
                log.info(ExecutorFactory.currentExecutorName() + " - " + finalI);
                if (RandomUtils.randomBoolean()) {
                    int random = RandomUtils.random(10, 500);
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(random));
                }
            }, 1000, 500, TimeUnit.MILLISECONDS);
        }

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(6));
    }


    @Test
    public void t3() {
        for (int i = 0; i < 3; i++) {
            executorFactory.driver("user" + i).execute(() -> {
                executorFactory.driver("map").submit(() -> {
                    log.info(ExecutorFactory.currentExecutorName() + " - 地图线程执行");
                }).thenAccept(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        log.info(ExecutorFactory.currentExecutorName() + " - 回到user线程");
                    }
                });
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

    @Test
    public void t4() {
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorFactory.driver("user" + i).execute(() -> {
                executorFactory.driver("map").submit(() -> {
                    log.info(ExecutorFactory.currentExecutorName() + " - 地图线程执行");
                    return "map" + finalI;
                }).thenAccept(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        log.info(ExecutorFactory.currentExecutorName() + " - 回到user线程: " + o);
                    }
                });
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

}
