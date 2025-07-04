package code.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class LoadingCacheTest {

    @Test
    public void c1() {
        /*2秒钟读取过期缓存*/
        LoadingCacheImpl<String, Object> cacheDriver = LoadingCacheImpl.<String, Object>builder()
                .expireAfterAccess(Duration.ofSeconds(2))
                .loader(key -> "value")
                .removalListener((key, value, cause) -> log.info("removalListener key:{} value:{} cause:{}", key, value, cause))
                .build()
                .start();

        log.info("{}", cacheDriver.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        cacheDriver.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        cacheDriver.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        cacheDriver.put("2", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        log.info("{}", cacheDriver.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));

    }

    @Test
    public void c2() {
        /**没有过期的缓存*/
        LoadingCacheImpl<String, Object> cacheDriver = LoadingCacheImpl.<String, Object>builder()
                .expireAfterAccess(null)
                .expireAfterWrite(null)
                .loader(key -> "value")
                .removalListener((key, value, cause) -> log.info("removalListener key:{} value:{} cause:{}", key, value, cause))
                .build()
                .start();

        log.info("{}", cacheDriver.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        cacheDriver.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        cacheDriver.put("1", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        cacheDriver.put("2", "o" + System.currentTimeMillis());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        log.info("{}", cacheDriver.get("1"));
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));

    }


    @Test
    public void c3() {
        /**没有过期的缓存*/
        LoadingCacheImpl<String, Object> loadingCache = LoadingCacheImpl.<String, Object>builder()
                .expireAfterWrite(Duration.ofSeconds(5))
                .expireHeartAfterWrite(Duration.ofSeconds(2))
                .loader(key -> "value")
                .heartListener((key, value, cause) -> log.info("heartListener key: {}, value: {}, cause: {}", key, value, cause))
                .removalListener((key, value, cause) -> log.info("removalListener key:{} value:{} cause:{}", key, value, cause))
                .build()
                .start();

        log.info("get: {}", loadingCache.get("1"));

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(8));
    }

}
