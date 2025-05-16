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
public class ExecutorServicePlatform extends ExecutorService {

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
        ExecutorJob executorJob;
        if (!(command instanceof ExecutorQueue)) {
            if (!(command instanceof ExecutorJob)) {
                executorJob = new ExecutorJob(command);
            } else {
                executorJob = (ExecutorJob) command;
            }

            if (!(command instanceof ExecutorJobScheduled.ScheduledExecutorJob) && executorJob.threadContext == null) {
                /*TODO 任务添加线程上下文*/
                executorJob.threadContext = new ThreadContext(ThreadContext.context());
            }

            if (executorJob instanceof IExecutorQueue iExecutorQueue) {
                if (Utils.isNotBlank(iExecutorQueue.queueName())) {
                    queueMap
                            .computeIfAbsent(iExecutorQueue.queueName(), k -> new ExecutorQueue(this))
                            .execute(executorJob);
                    return;
                }
            }
        } else {
            executorJob = (ExecutorJob) command;
        }
        threadPoolExecutor.execute(executorJob);
    }

}
