package run;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.LogbackUtil;
import wxdgaming.boot2.core.cache.Cache;
import wxdgaming.boot2.core.lang.DiffTime;
import wxdgaming.boot2.core.threading.ExecutorConfig;
import wxdgaming.boot2.core.threading.ExecutorUtil;
import wxdgaming.boot2.core.util.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * 缓存效率测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-07 10:47
 **/
public class CASCacheTest {

    public static void main(String[] args) throws InterruptedException {

        ExecutorUtil.getInstance().init();
        LogbackUtil.refreshLoggerLevel(Level.INFO);
        t0();
        t0();
        t0();
        t1();
        t1();
        t1();
    }

    public static void t0() throws InterruptedException {
        Cache<Long, Long> cache = Cache.<Long, Long>builder()
                .cacheName("CAS")
                .cacheType(Cache.CacheType.CAS)
                .delay(1000)
                .expireAfterWrite(10000)
                .loader((k) -> k)
                .removalListener((k, v) -> true)
                .heartTime(10000)
                .hashArea(10)
                .build();

        DiffTime diffTime = new DiffTime();

        int c1 = 100_0000;
        long max = 65535;

        for (int i = 0; i < c1; i++) {
            Long ifPresent = cache.getIfPresent(RandomUtils.random(max));
        }
        float diff = diffTime.diff();
        System.out.println("Cache " + cache.getCacheName() + " 单线程 随机访问：" + cache.cacheSize() + ", " + c1 + " 次 耗时:" + diff + " ms");
        cache.shutdown();
    }

    public static void t1() throws InterruptedException {
        Cache<Long, Long> cache = Cache.<Long, Long>builder()
                .cacheName("CAS")
                .cacheType(Cache.CacheType.CAS)
                .delay(1000)
                .expireAfterWrite(10000)
                .loader((k) -> k)
                .removalListener((k, v) -> true)
                .heartTime(10000)
                .hashArea(10)
                .build();

        DiffTime diffTime = new DiffTime();

        int c1 = 100_0000;
        long max = 65535;
        CountDownLatch latch = new CountDownLatch(c1);

        for (int i = 0; i < c1; i++) {
            ExecutorUtil.getInstance().getLogicExecutor().execute(() -> {
                Long ifPresent = cache.getIfPresent(RandomUtils.random(max));
                latch.countDown();
            });
        }
        latch.await();
        float diff = diffTime.diff();
        System.out.println("Cache " + cache.getCacheName() + " 多线程 随机访问：" + cache.cacheSize() + ", " + c1 + " 次 耗时:" + diff + " ms");
        cache.shutdown();
    }

}
