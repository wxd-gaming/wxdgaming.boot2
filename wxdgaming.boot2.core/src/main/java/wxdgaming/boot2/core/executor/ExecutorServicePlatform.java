package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 09:03
 **/
@Slf4j
public class ExecutorServicePlatform implements ExecutorService {

    protected ThreadPoolExecutor threadPoolExecutor;
    protected ConcurrentMap<String, ExecutorQueue> queueMap = new ConcurrentHashMap<>();

    ExecutorServicePlatform(String namePrefix, int threadSize) {
        threadPoolExecutor = new ThreadPoolExecutor(
                threadSize, threadSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new NameThreadFactory(namePrefix)
        );
    }

    @Override public void execute(Runnable command) {
        if (command instanceof IExecutorQueue iExecutorQueue) {
            if (Utils.isNotBlank(iExecutorQueue.queueName())) {
                queueMap
                        .computeIfAbsent(iExecutorQueue.queueName(), k -> new ExecutorQueue(this))
                        .execute(command);
                return;
            }
        }
        if (!(command instanceof ExecutorJob)) {
            command = new ExecutorJob(command);
        }
        threadPoolExecutor.execute(command);
    }

}
