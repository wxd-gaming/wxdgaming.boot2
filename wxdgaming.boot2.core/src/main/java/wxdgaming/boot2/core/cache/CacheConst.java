package wxdgaming.boot2.core.cache;

import wxdgaming.boot2.core.executor.ExecutorFactory;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 缓存常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 15:11
 **/
public interface CacheConst {

    ScheduledExecutorService scheduledExecutorService = ExecutorFactory.newSingleThreadScheduledExecutor("cache-scheduled");
    Duration minHeartDuration = Duration.ofSeconds(5);
    Duration outerDuration = Duration.ofSeconds(2);
    Duration heartDurationDefault = Duration.ofSeconds(5);

}
