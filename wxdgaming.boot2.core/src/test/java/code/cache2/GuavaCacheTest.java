package code.cache2;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.format.ByteFormat;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.util.RandomUtils;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class GuavaCacheTest {

    static int area = 5;
    static long heartTimeMs = 50000;
    static long expireTimeMs = 1200000;
    static long maxSize = 655350;
    static int sleepTimeMs = 12000;

    @BeforeEach
    void beforeEach() {
        if (ExecutorFactory.Lazy.instance != null) return;
        ExecutorProperties executorProperties = new ExecutorProperties();
        ExecutorConfig logic = new ExecutorConfig();
        logic.setCoreSize(12).setMaxQueueSize(500000).setWarnSize(500000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
        executorProperties.setLogic(logic);
        new ExecutorFactory(executorProperties);
    }

    @Test
    public void t1() {

        GuavaCacheImpl<String, Object> caffeineCacheImpl = GuavaCacheImpl.<String, Object>builder()
                .loader(new CacheLoader<String, Object>() {
                    @Override public Object load(String key) throws Exception {
                        if ("1".equals(key)) return null;
                        log.info("cache loading key:{}", key);
                        return key;
                    }
                })
                .removeDuration(Duration.ofSeconds(18))
                .heartDuration(Duration.ofSeconds(6))
                .heartListener((RemovalNotification<String, Object> notification) -> log.info("心跳处理 key: {}, value: {}, cause: {}", notification.getKey(), notification.getValue(), notification.getCause()))
                .removalListener((RemovalNotification<String, Object> notification) -> log.info("移除过期 key: {}, value: {}, cause: {}", notification.getKey(), notification.getValue(), notification.getCause()))
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
        GuavaCacheImpl<Long, Object> guavaCache = GuavaCacheImpl.<Long, Object>builder()
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
        singleThread(guavaCache);
        multiThread(guavaCache);
    }

    public void singleThread(GuavaCacheImpl<Long, Object> cache) {
        cache.invalidateAll();
        DiffTimeRecord diffTime = DiffTimeRecord.start4Ms();
        Object string = "";
        for (long i = 0; i < maxSize; i++) {
            string = cache.get(RandomUtils.random(maxSize));
        }
        log.info(
                "{} 单线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{}",
                cache.getCacheName(), maxSize, cache.size(), JSON.toJSONString(string), diffTime.interval()
        );
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

    public void multiThread(GuavaCacheImpl<Long, Object> cache) throws Exception {
        cache.invalidateAll();
        DiffTimeRecord diffTime = DiffTimeRecord.start4Ms();
        AtomicReference string = new AtomicReference();
        CountDownLatch latch = new CountDownLatch((int) maxSize);
        for (long i = 0; i < maxSize; i++) {
            ExecutorFactory.getExecutorServiceLogic().execute(() -> {
                string.set(cache.get(RandomUtils.random(maxSize)));
                latch.countDown();
            });
        }
        latch.await();
        log.info(
                "{} 多线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{}",
                cache.getCacheName(), maxSize, cache.size(), JSON.toJSONString(string.get()), diffTime.interval()
        );
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

}
