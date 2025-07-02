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

        CaffeineCacheData caffeineCacheData = new CaffeineCacheData()
                .setLoader(key -> "1".equals(key) ? null : key)
                .setHeartDuration(Duration.ofSeconds(1))
                .setHeartListener((key, value, cause) -> log.info("心跳处理 key: {}, value: {}, cause: {}", key, value, cause))
                .setRemovalListener((key, value, cause) -> log.info("移除过期 key: {}, value: {}, cause: {}", key, value, cause))
                .build();

        for (int i = 0; i < 10; i++) {
            log.info("get " + String.valueOf((Object) caffeineCacheData.get("1")));
            log.info("get " + String.valueOf((Object) caffeineCacheData.get("2")));
            log.info("get " + String.valueOf((Object) caffeineCacheData.get("3")));
            log.info("==========================");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        }

    }

}
