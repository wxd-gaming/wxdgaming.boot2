package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 执行器监视
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 14:46
 **/
@Slf4j
class ExecutorMonitor extends Thread {

    public static ConcurrentHashMap<Thread, JobContent> executorJobConcurrentHashMap = new ConcurrentHashMap<>();

    public static void put(ExecutorJob executorJob) {
        executorJobConcurrentHashMap.put(Thread.currentThread(), new JobContent(executorJob, System.currentTimeMillis()));
    }

    public static void release() {
        Thread thread = Thread.currentThread();
        JobContent jobContent = executorJobConcurrentHashMap.remove(thread);
        if (jobContent == null) return;
        long diff = System.currentTimeMillis() - jobContent.start();
        if (diff > 150) {
            log.warn(
                    "线程: {}, 执行器: {}, 执行时间: {}ms",
                    thread.getName(), jobContent.executorJob().getStack(), diff
            );
        }
    }


    public ExecutorMonitor() {
        super("executor-monitor");
        this.start();
    }

    @Override public void run() {
        while (!Thread.interrupted()) {
            try {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
                for (Map.Entry<Thread, JobContent> entry : executorJobConcurrentHashMap.entrySet()) {
                    Thread thread = entry.getKey();
                    JobContent jobContent = entry.getValue();
                    long diff = System.currentTimeMillis() - jobContent.start();
                    if (diff > TimeUnit.SECONDS.toMillis(30)) {
                        log.warn(
                                "线程执行器监视, 线程: {}, 执行器: {}, 执行时间: {}ms\n{}",
                                thread.getName(), jobContent.executorJob().getStack(), diff,
                                Utils.stack(thread.getStackTrace())
                        );
                    }
                }
            } catch (Throwable throwable) {
                log.error("线程执行器监视", throwable);
            }
        }
    }

    public record JobContent(ExecutorJob executorJob, long start) {

    }

}
