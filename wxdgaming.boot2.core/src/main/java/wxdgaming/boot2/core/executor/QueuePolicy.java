package wxdgaming.boot2.core.executor;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * 队列策略
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-30 15:56
 **/
public interface QueuePolicy {

    RejectedExecutionHandler getRejectedExecutionHandler();

    @SuppressWarnings({"rawtypes"})
    void execute(ArrayBlockingQueue queue, Runnable task);

}
