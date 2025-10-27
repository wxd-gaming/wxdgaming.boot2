package code.cache2;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.*;
import wxdgaming.boot2.core.util.RandomUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class CaffeineCacheTest {

    static int threadSize = 64;
    static long readSize = 65535;
    static long maxSize = 100;


    @BeforeEach
    void beforeEach() {
        if (ExecutorFactory.Lazy.instance != null) return;
        ExecutorProperties executorProperties = new ExecutorProperties();
        ExecutorConfig logic = new ExecutorConfig();
        logic.setCoreSize(threadSize).setMaxQueueSize(500000).setWarnSize(500000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
        executorProperties.setLogic(logic);
        new ExecutorFactory(executorProperties);
    }

    @Test
    public void t1() {

        CaffeineCacheImpl<String, Object> caffeineCacheImpl = CaffeineCacheImpl.<String, Object>builder()
                .loader(new CacheLoader<String, Object>() {
                    @Override public @Nullable Object load(String key) throws Exception {
                        if ("1".equals(key)) return null;
                        log.info("cache loading key:{}", key);
                        return key;
                    }
                })
                .removeDuration(Duration.ofSeconds(18))
                .heartDuration(Duration.ofSeconds(6))
                .heartListener((key, value, cause) -> log.info("心跳处理 key: {}, value: {}, cause: {}", key, value, cause))
                .removalListener((key, value, cause) -> log.info("移除过期 key: {}, value: {}, cause: {}", key, value, cause))
                .build();

        for (int i = 0; i < 2; i++) {
            log.info("get " + caffeineCacheImpl.get("1"));
            log.info("get " + caffeineCacheImpl.get("2"));
            log.info("get " + caffeineCacheImpl.get("3"));
            caffeineCacheImpl.put("2", "e" + System.currentTimeMillis());
            log.info("==========================");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
        }
        System.out.println("----------------------------");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(18));
        caffeineCacheImpl.invalidateAll();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
    }

    @RepeatedTest(5)
    public void c10() throws Exception {
        /*2秒钟读取过期缓存*/
        CaffeineCacheImpl<Long, Object> caffeineCacheImpl = CaffeineCacheImpl.<Long, Object>builder()
                .cacheName("test")
                .loader(new CacheLoader<Long, Object>() {
                    @Override public @Nullable Object load(Long key) throws Exception {
                        if ("1".equals(key)) return null;
                        //                        log.info("cache loading key:{}", key);
                        return key;
                    }
                })
                .removeDuration(Duration.ofSeconds(18))
                .heartDuration(Duration.ofSeconds(6))
                //                .heartListener((key, value, cause) -> log.info("心跳处理 key: {}, value: {}, cause: {}", key, value, cause))
                //                .removalListener((key, value, cause) -> log.info("移除过期 key: {}, value: {}, cause: {}", key, value, cause))
                .build();
        ThreadStopWatch.release();
        ThreadStopWatch.init("缓存测试");
        singleThread(caffeineCacheImpl);
        multiThread(caffeineCacheImpl);
        log.debug("{}", ThreadStopWatch.prettyPrint());
    }

    public void singleThread(CaffeineCacheImpl<Long, Object> cache) {
        cache.invalidateAll();
        ThreadStopWatch.start("循环 " + readSize + " 次数");
        AtomicReference<Object> string = new AtomicReference<>();
        for (long i = 0; i < readSize; i++) {
            string.set(cache.get(RandomUtils.random(maxSize)));
        }
        ThreadStopWatch.stop();
        log.info(
                "{} 单线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}",
                cache.getCacheName(), readSize, cache.size(), JSON.toJSONString(string)
        );
    }

    public void multiThread(CaffeineCacheImpl<Long, Object> cache) throws Exception {
        cache.invalidateAll();
        ThreadStopWatch.start("循环 " + readSize + " 次数");
        AtomicReference<Object> string = new AtomicReference<>();
        ThreadExecutorLatch.threadLocalInit();
        for (long i = 0; i < readSize; i++) {
            ThreadExecutorLatch.executor(() -> string.set(cache.get(RandomUtils.random(maxSize))), ExecutorFactory.getExecutorServiceLogic());
        }
        ThreadExecutorLatch.await();
        ThreadExecutorLatch.release();
        ThreadStopWatch.stop();
        log.info(
                "{} 多线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}",
                cache.getCacheName(), readSize, cache.size(), JSON.toJSONString(string.get())
        );
    }

    @Test
    public void tt() throws InterruptedException {
        ExecutorLatch executorLatch = new ExecutorLatch();

        executorLatch.executor(
                () -> {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                ExecutorFactory.getExecutorServiceLogic()
        );

        executorLatch.await();

    }

}
