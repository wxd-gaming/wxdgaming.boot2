package executor4;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.value.qual.StringVal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.util.RandomUtils;

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

    ExecutorDriverService executorDriverService;

    @BeforeEach
    public void startUp() {
        executorDriverService = new ExecutorDriverService("test", 4);
        for (int i = 0; i < 10; i++) {
            executorDriverService.driver("d" + (i + 1));
        }
    }

    @Test
    public void t1() {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorDriverService.driver("d" + (i % 10)).execute(() -> {
                System.out.println(ExecutorFactory.currentExecutorName() + " - " + finalI);
            });
        }
    }

    @Test
    public void t2() {
        log.info("start");
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorDriverService.driver("d" + (i % 10)).scheduleWithFixedDelay(() -> {
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
            executorDriverService.driver("user" + i).execute(() -> {
                executorDriverService.driver("map").submit(() -> {
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
        for (int i = 0; i < 10; i++) {
            int finalI = i % 3;
            executorDriverService.driver("user" + finalI).execute(() -> {
                log.info("{} - 当前线程打印", ExecutorFactory.currentExecutorName());
                executorDriverService.driver("map" + finalI).submit(() -> {
                    log.info("{} - 地图线程执行", ExecutorFactory.currentExecutorName());
                    return "map" + finalI;
                }).thenAccept(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        log.info("{} - 回到user线程: {}", ExecutorFactory.currentExecutorName(), o);
                    }
                });
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

    /**
     * 执行带有回调，回到当前线程的异步延迟任务
     */
    @Test
    public void t5() {
        for (int i = 0; i < 3; i++) {
            executorDriverService.driver("user" + i).execute(() -> {
                RunnableFuture runnableFuture = new RunnableFuture(() -> {
                    log.info("{} - 我是模拟飞行特效", ExecutorFactory.currentExecutorName());
                });
                runnableFuture.whenComplete((obj, throwable) -> {
                    log.info(ExecutorFactory.currentExecutorName() + " - obj: " + obj + ", throwable: " + throwable);
                });
                executorDriverService.driver("map").schedule(runnableFuture, 1000, TimeUnit.MILLISECONDS);
            });
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

    /**
     * 执行带有回调，回到当前线程的异步延迟任务
     */
    @Test
    public void t6() {
        for (int i = 0; i < 3; i++) {
            int finalI = i % 3;
            /*执行用户请求*/
            ReqHandler reqHandler = new ReqHandler(String.valueOf(finalI));
            executorDriverService.driver("user" + finalI).execute(reqHandler);
        }
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

    protected class ReqHandler implements Runnable {

        private String req;

        public ReqHandler(String req) {
            this.req = req;
        }

        @Override
        public void run() {
            SupplierFuture<String> runnableFuture = new SupplierFuture<>(() -> {
                log.info("{} - 我是模拟飞行特效 {}", ExecutorFactory.currentExecutorName(), req);
                return String.valueOf(req);
            });
            /*地图执行飞行特效完成后回到用户线程执行后续操作*/
            runnableFuture.whenComplete((obj, throwable) -> {
                log.info("{} - obj: {}, throwable: {}", ExecutorFactory.currentExecutorName(), obj, String.valueOf(throwable));
            });
            /*我们需要在地图内执行一个飞行特效*/
            executorDriverService.driver("map").schedule(runnableFuture, 1000, TimeUnit.MILLISECONDS);
        }
    }

}
