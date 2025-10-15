package code.cache;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.format.ByteFormat;
import wxdgaming.boot2.core.function.Consumer3;
import wxdgaming.boot2.core.lang.DiffTimeRecord;
import wxdgaming.boot2.core.util.RandomUtils;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class CacheDriverTest {

    static int area = 5;
    static long heartTimeMs = 50000;
    static long expireTimeMs = 1200000;
    static long readSize = 65535;
    static long maxSize = 600;
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
    public void c1() {
        /*2秒钟读取过期缓存*/
        CacheDriverImpl<String, Object> cache = CacheDriverImpl.<String, Object>builder()
                .expireAfterAccess(Duration.ofSeconds(16))
                .heartExpireAfterWrite(Duration.ofSeconds(5))
                .loader(key -> key)
                .heartListener(new Consumer3<String, Object, CacheDriver.RemovalCause>() {
                    @Override public void accept(String string, Object object, CacheDriver.RemovalCause removalCause) {
                        log.info("心跳事件 key:{} value:{} cause:{}", string, object, removalCause);
                    }
                })
                .removalListener((key, value, cause) -> {
                    log.info("移除事件 key:{} value:{} cause:{}", key, value, cause);
                    return true;
                })
                .build();
        for (int i = 0; i < 2; i++) {
            log.info("get " + cache.get("1"));
            log.info("get " + cache.get("2"));
            log.info("get " + cache.get("3"));
            cache.put("2", "e" + System.currentTimeMillis());
            log.info("==========================");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
        }
        System.out.println("----------------------------");
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(18));
        cache.invalidateAll();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
        log.info("size:{}", cache.size());
    }

    @Test
    public void c2() {
        /*没有过期的缓存*/
        CacheDriverImpl<String, Object> cache = CacheDriverImpl.<String, Object>builder()
                .expireAfterAccess(null)
                .expireAfterWrite(null)
                .loader(key -> "value")
                .build();

        log.info("{}", cache.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        cache.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        cache.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
        cache.put("2", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
        log.info("{}", cache.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(18));
        log.info("size:{}", cache.size());
    }


    @Test
    public void c3() {
        /*没有过期的缓存*/
        CacheDriverImpl<String, Object> loadingCache = CacheDriverImpl.<String, Object>builder()
                .expireAfterWrite(Duration.ofSeconds(5))
                .heartExpireAfterWrite(Duration.ofSeconds(2))
                .loader(key -> "value")
                .heartListener((key, value, cause) -> log.info("heartListener key: {}, value: {}, cause: {}", key, value, cause))
                .removalListener((key, value, cause) -> {
                    log.info("removalListener key:{} value:{} cause:{}", key, value, cause);
                    return true;
                })
                .build();

        log.info("get: {}", loadingCache.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
    }

    @RepeatedTest(5)
    public void c10() throws Exception {
        /*2秒钟读取过期缓存*/
        CacheDriverImpl<Long, Object> cache = CacheDriverImpl.<Long, Object>builder()
                .cacheName("test")
                .blockSize(32)
                .expireAfterAccess(Duration.ofSeconds(16))
                .heartExpireAfterWrite(Duration.ofSeconds(5))
                .loader(key -> key)
                .heartListener((string, object, removalCause) -> {
                    //                    log.info("心跳事件 key:{} value:{} cause:{}", string, object, removalCause);
                })
                .removalListener((key, value, cause) -> {
                    //                    log.info("移除事件 key:{} value:{} cause:{}", key, value, cause);
                    return true;
                })
                .build();
        singleThread(cache);
        multiThread(cache);
    }

    public void singleThread(CacheDriverImpl<Long, Object> cache) {
        cache.invalidateAll();
        DiffTimeRecord diffTime = DiffTimeRecord.start4Ms();
        Object string = "";
        for (long i = 0; i < readSize; i++) {
            string = cache.get(RandomUtils.random(maxSize));
        }
        log.info(
                "{} 单线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{}",
                cache.getCacheName(), readSize, cache.size(), JSON.toJSONString(string), diffTime.interval()
        );
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

    public void multiThread(CacheDriverImpl<Long, Object> cache) throws Exception {
        cache.invalidateAll();
        DiffTimeRecord diffTime = DiffTimeRecord.start4Ms();
        AtomicReference string = new AtomicReference();
        CountDownLatch latch = new CountDownLatch((int) readSize);
        for (long i = 0; i < readSize; i++) {
            ExecutorFactory.getExecutorServiceLogic().execute(() -> {
                string.set(cache.get(RandomUtils.random(maxSize)));
                latch.countDown();
            });
        }
        latch.await();
        log.info(
                "{} 多线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{}",
                cache.getCacheName(), readSize, cache.size(), JSON.toJSONString(string.get()), diffTime.interval()
        );
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

}
