package code.cache2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.LogbackUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.cache2.Cache;
import wxdgaming.boot2.core.cache2.LRUCache;
import wxdgaming.boot2.core.cache2.LRULongCache;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.format.ByteFormat;
import wxdgaming.boot2.core.function.Function1;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Cache2Test {

    static int area = 5;
    static long heartTimeMs = 50000;
    static long expireTimeMs = 1200000;
    static long maxSize = 655350;
    static int sleepTimeMs = 12000;

    static {
        LogbackUtil.refreshLoggerLevel(Level.INFO);
    }

    @Test
    public void LRULongCache() throws InterruptedException {
        LRULongCache<String> builder = LRULongCache.<String>builder()
                .cacheName("LRULongCache")
                .area(area)
                .heartTimeMs(heartTimeMs)
                .expireAfterWriteMs(expireTimeMs)
                .loader(new Function1<Long, String>() {
                    @Override public String apply(Long aLong) {
                        return String.valueOf(aLong);
                    }
                })
                .removalListener((k, v) -> {
                    // log.info("删除了 {} {}", k, v);
                    return true;
                })
                .build();
        builder.start();
        singleThread(builder);
        multiThread(builder);
    }

    @Test
    public void LRUCache() throws InterruptedException {
        LRUCache<Long, String> builder = LRUCache.<Long, String>builder()
                .cacheName("LRUCache")
                .area(area)
                .heartTimeMs(heartTimeMs)
                .expireAfterWriteMs(expireTimeMs)
                .loader(new Function1<Long, String>() {
                    @Override public String apply(Long aLong) {
                        return String.valueOf(aLong);
                    }
                })
                .removalListener((k, v) -> {
                    // log.info("删除了 {} {}", k, v);
                    return true;
                })
                .build();
        builder.start();
        singleThread(builder);
        multiThread(builder);
    }

    @Test
    public void CASCache() throws InterruptedException {
        CASCache<Long, String> builder = CASCache.<Long, String>builder()
                .cacheName("CASCache")
                .area(area)
                .heartTimeMs(heartTimeMs)
                .expireAfterWriteMs(expireTimeMs)
                .loader(new Function1<Long, String>() {
                    @Override public String apply(Long aLong) {
                        return String.valueOf(aLong);
                    }
                })
                .removalListener((k, v) -> {
                    // log.info("删除了 {} {}", k, v);
                    return true;
                })
                .build();
        builder.start();
        singleThread(builder);
        multiThread(builder);
    }

    @Test
    public void LRULong2LongCache() throws InterruptedException {
        LRULongCache<Long> builder = LRULongCache.<Long>builder()
                .cacheName("LRULong2LongCache")
                .area(area)
                .heartTimeMs(heartTimeMs)
                .expireAfterWriteMs(expireTimeMs)
                .loader(new Function1<Long, Long>() {
                    @Override public Long apply(Long aLong) {
                        return aLong;
                    }
                })
                .removalListener((k, v) -> {
                    // log.info("删除了 {} {}", k, v);
                    return true;
                })
                .build();
        builder.start();
        singleThread(builder);
        multiThread(builder);
    }

    public void singleThread(Cache cache) {
        cache.discardAll();
        DiffTime diffTime = new DiffTime();
        Object string = "";
        for (long i = 0; i < maxSize; i++) {
            string = cache.getIfPresent(RandomUtils.random(maxSize));
        }
        log.info("{} 单线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{} ms", cache.getCacheName(), maxSize, cache.size(), JSON.toJSONString(string), diffTime.diffMs5());
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

    public void multiThread(Cache cache) throws InterruptedException {
        cache.discardAll();
        DiffTime diffTime = new DiffTime();
        AtomicReference string = new AtomicReference();
        CountDownLatch latch = new CountDownLatch((int) maxSize);
        for (long i = 0; i < maxSize; i++) {
            ExecutorFactory.getExecutorServiceLogic().execute(() -> {
                string.set(cache.getIfPresent(RandomUtils.random(maxSize)));
                latch.countDown();
            });
        }
        latch.await();
        log.info("{} 多线程随机访问：{} 次, 缓存数量：{}, 最后一次访问结果：{}, 耗时：{} ms", cache.getCacheName(), maxSize, cache.size(), JSON.toJSONString(string.get()), diffTime.diffMs5());
        log.info("{} 缓存数量：{}, 内存 {}", cache.getCacheName(), cache.size(), ByteFormat.format(cache.memorySize()));
    }

}
