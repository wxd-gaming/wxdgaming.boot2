package code.cache2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class CaffeineCacheTest {

    @Test
    public void t1() {

        CaffeineCacheImpl<String, Object> caffeineCacheImpl = CaffeineCacheImpl.builder()
                .loader(key -> "1".equals(key) ? null : key)
                .heartDuration(Duration.ofSeconds(1))
                .heartListener((key, value, cause) -> log.info("心跳处理 key: {}, value: {}, cause: {}", key, value, cause))
                .removalListener((key, value, cause) -> log.info("移除过期 key: {}, value: {}, cause: {}", key, value, cause))
                .build()
                .start();

        for (int i = 0; i < 2; i++) {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
            log.info("get " + caffeineCacheImpl.get("1"));
            log.info("get " + caffeineCacheImpl.get("2"));
            log.info("get " + caffeineCacheImpl.get("3"));
            caffeineCacheImpl.put("2", "e"+System.currentTimeMillis());
            log.info("==========================");
        }
        caffeineCacheImpl.invalidateAll();
    }

}
