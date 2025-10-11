package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.runtime.RunTimeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 执行器监视
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 14:46
 **/
@Slf4j
public final class ExecutorMonitor extends Thread {

    public static ConcurrentHashMap<Thread, JobContent> executorJobConcurrentHashMap = new ConcurrentHashMap<>();

    public static void put(ExecutorJob executorJob) {
        executorJobConcurrentHashMap.put(Thread.currentThread(), new JobContent(executorJob));
    }

    public static void release() {
        Thread thread = Thread.currentThread();
        JobContent jobContent = executorJobConcurrentHashMap.remove(thread);
        if (jobContent == null) return;
        long diffNs = System.nanoTime() - jobContent.startTime;
        ExecutorJob executorJob = jobContent.executorJob;
        String stack = executorJob.getStack();
        if (!executorJob.isIgnoreRunTimeRecord()) {
            RunTimeUtil.record(stack, jobContent.startTime);
        }
        long diffMs = TimeUnit.NANOSECONDS.toMillis(diffNs);
        if (diffMs > 150) {
            log.warn(
                    "线程: {}, 执行器: {}, 执行时间: {}ms",
                    thread.getName(), stack, diffMs
            );
        }
    }

    @Getter AtomicBoolean exit = new AtomicBoolean(false);

    public ExecutorMonitor() {
        super("executor-monitor");
        this.start();
    }

    @Override public void run() {
        while (!exit.get()) {
            try {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
                for (Map.Entry<Thread, JobContent> entry : executorJobConcurrentHashMap.entrySet()) {
                    Thread thread = entry.getKey();
                    JobContent jobContent = entry.getValue();
                    long nanoTime = System.nanoTime();
                    if ((nanoTime - jobContent.lastMonitorTime) > TimeUnit.SECONDS.toNanos(10)) {
                        jobContent.lastMonitorTime = nanoTime;
                        long diff = TimeUnit.NANOSECONDS.toSeconds(nanoTime - jobContent.startTime);
                        log.warn(
                                "线程执行器监视, 线程: {}, 执行器: {}, 执行时间: {}s, 堆栈：{}",
                                thread.getName(), jobContent.executorJob.getStack(), diff,
                                StackUtils.stack(thread.getStackTrace())
                        );
                    }
                }
            } catch (Throwable throwable) {
                log.error("线程执行器监视", throwable);
            }
        }
    }

    public static final class JobContent extends ObjectBase {

        final ExecutorJob executorJob;
        final long startTime;
        long lastMonitorTime;

        public JobContent(ExecutorJob executorJob) {
            this.executorJob = executorJob;
            this.startTime = this.lastMonitorTime = System.nanoTime();
        }

    }

}
