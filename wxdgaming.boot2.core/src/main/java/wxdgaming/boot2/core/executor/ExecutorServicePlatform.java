package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;

/**
 * 执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 09:03
 **/
@Slf4j
public class ExecutorServicePlatform extends ExecutorService {

    private final String namePrefix;
    private final int queueSize;
    private final int warnSize;
    private final QueuePolicy queuePolicy;
    protected ThreadPoolExecutor threadPoolExecutor;
    protected ConcurrentMap<String, ExecutorQueue> queueMap = new ConcurrentHashMap<>();

    /** 如果队列已经达到上限默认是拒绝添加任务的 */
    ExecutorServicePlatform(String namePrefix, int threadSize, int queueSize, int warnSize, QueuePolicy queuePolicy) {
        this.namePrefix = namePrefix;
        this.queueSize = queueSize;
        this.warnSize = warnSize;
        this.queuePolicy = queuePolicy;
        threadPoolExecutor = new ThreadPoolExecutor(
                threadSize, threadSize,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize),
                new NameThreadFactory(namePrefix, false),
                queuePolicy.getRejectedExecutionHandler()
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
                executorJob.threadContext.remove("queue");
                executorJob.threadContext.remove("queueName");
            }

            if (executorJob instanceof IExecutorQueue iExecutorQueue) {
                if (StringUtils.isNotBlank(iExecutorQueue.queueName())) {
                    queueMap
                            .computeIfAbsent(iExecutorQueue.queueName(), k -> new ExecutorQueue(k, this, this.queueSize, warnSize, this.queuePolicy))
                            .execute(executorJob);
                    return;
                }
            }
        } else {
            executorJob = (ExecutorJob) command;
        }
        int size = threadPoolExecutor.getQueue().size();
        if (size > warnSize) {
            log.warn("ExecutorService {} queueSize:{}, {}", namePrefix, warnSize, command);
        }
        threadPoolExecutor.execute(executorJob);
    }

    @Override public void stop() {
        threadPoolExecutor.shutdown();
    }


}
