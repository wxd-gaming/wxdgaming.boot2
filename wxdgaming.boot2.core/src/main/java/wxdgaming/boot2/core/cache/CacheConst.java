package wxdgaming.boot2.core.cache;

import wxdgaming.boot2.core.executor.AbstractExecutorService;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

import java.time.Duration;

/**
 * 缓存常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 15:11
 **/
public interface CacheConst {

    AbstractExecutorService scheduledExecutorService = ExecutorFactory.createPlatform(
            "cache-scheduled",
            1, 10000,
            QueuePolicyConst.AbortPolicy
    );

    Duration minHeartDuration = Duration.ofSeconds(5);
    Duration outerDuration = Duration.ofSeconds(2);
    Duration heartDurationDefault = Duration.ofSeconds(5);

}
