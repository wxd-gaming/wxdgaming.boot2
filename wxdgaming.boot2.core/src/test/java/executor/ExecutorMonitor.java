package executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程监视器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 19:10
 **/
@Slf4j
public class ExecutorMonitor extends Thread {

    static ConcurrentHashMap<Thread, ExecutorMonitorContext> executorContextConcurrentHashMap = new ConcurrentHashMap<>();

    public static ExecutorMonitorContext threadContext() {
        return executorContextConcurrentHashMap.computeIfAbsent(Thread.currentThread(), ExecutorMonitorContext::new);
    }

    public static void cleanup() {
        ExecutorMonitorContext executorMonitorContext = executorContextConcurrentHashMap.remove(Thread.currentThread());
        if (executorMonitorContext != null) {
            long l = executorMonitorContext.costMillis();
            if (l > 30) {
                log.error("线程卡住：\n{}", executorMonitorContext.toString());
            }
        }
    }

}
