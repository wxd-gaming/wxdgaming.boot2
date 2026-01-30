package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-27 09:23
 **/
@Slf4j
public class ExecutorFactory {

    static class Lazy {

        private static final Timer TIMER;

        static final ConcurrentHashMap<String, AbstractExecutorService> executorServiceMap = new ConcurrentHashMap<>();
        static final ConcurrentHashMap<Thread, ExecutorContext.Content> runnableMonitorMap = new ConcurrentHashMap<>();

        static {
            TIMER = new Timer("ExecutorFactory-Timer");
            long durationMillis = Duration.ofSeconds(10).toMillis();
            TIMER.schedule(
                    new TimerTask() {
                        @Override public void run() {
                            try {
                                Collection<ExecutorContext.Content> values = runnableMonitorMap.values();
                                for (ExecutorContext.Content executorContent : values) {
                                    long startTime = executorContent.getStartTime();
                                    if (startTime < Integer.MAX_VALUE) {
                                        continue;
                                    }
                                    long cost = System.nanoTime() - startTime;
                                    if (TimeUnit.NANOSECONDS.toSeconds(cost) < 5) {
                                        continue;
                                    }
                                    Thread thread = executorContent.getThread();
                                    StackTraceElement[] stackTrace = thread.getStackTrace();
                                    String stack = StackUtils.stack(stackTrace);
                                    long millis = TimeUnit.NANOSECONDS.toMillis(cost);
                                    String s = DurationFormatUtils.formatDuration(millis, "HH:mm:ss.SSS");
                                    log.error("线程卡住 {} 运行: {}, stack: {}", executorContent.toString(), s, stack);
                                }
                            } catch (Exception e) {
                                log.info("定时任务异常", e);
                            }
                        }
                    },
                    durationMillis, durationMillis
            );
        }

        public static AbstractExecutorService getExecutorServiceBase() {
            return executorServiceMap.computeIfAbsent("base", l -> {
                return new ExecutorServicePlatform(
                        "base",
                        2, 5000,
                        QueuePolicyConst.AbortPolicy
                );
            });
        }

        public static AbstractExecutorService getExecutorServiceLogic() {
            return executorServiceMap.computeIfAbsent("logic", l -> {
                int availableProcessors = Runtime.getRuntime().availableProcessors();
                return new ExecutorServicePlatform(
                        "logic",
                        availableProcessors, availableProcessors * 100,
                        QueuePolicyConst.AbortPolicy
                );
            });
        }

        public static AbstractExecutorService getExecutorServiceVirtual() {
            return executorServiceMap.computeIfAbsent("virtual", l -> {
                /*最大值不得超过500*/
                int availableProcessors = Math.min(Runtime.getRuntime().availableProcessors() * 50, 500);
                return new ExecutorServiceVirtual(
                        "virtual",
                        availableProcessors, availableProcessors * 100,
                        QueuePolicyConst.AbortPolicy
                );
            });
        }

    }

    public static void init(ExecutorProperties executorProperties) {
        log.debug("初始化线程池... base {}", executorProperties.getBasic());
        createPlatform("base", executorProperties.getBasic());
        log.debug("初始化线程池... logic {}", executorProperties.getLogic());
        createPlatform("base", executorProperties.getLogic());
        log.debug("初始化线程池... virtual {}", executorProperties.getVirtual());
        createVirtual("virtual", executorProperties.getVirtual());
    }

    public static void exit() {
        Lazy.TIMER.cancel();
    }

    public static AbstractExecutorService getExecutorServiceBasic() {
        return Lazy.getExecutorServiceBase();
    }

    public static AbstractExecutorService getExecutorServiceLogic() {
        return Lazy.getExecutorServiceLogic();
    }

    public static AbstractExecutorService getExecutorServiceVirtual() {
        return Lazy.getExecutorServiceVirtual();
    }

    public static AbstractExecutorService createPlatform(String name, ExecutorConfig executorConfig) {
        return createPlatform(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    public static ExecutorServicePlatform createPlatform(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(namePrefix, threadSize, queueSize, queuePolicy);
        ExecutorFactory.Lazy.executorServiceMap.put(namePrefix, executorServicePlatform);
        return executorServicePlatform;
    }

    public static AbstractExecutorService createVirtual(String name, ExecutorConfig executorConfig) {
        return createVirtual(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    public static ExecutorServiceVirtual createVirtual(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(namePrefix, threadSize, queueSize, queuePolicy);
        ExecutorFactory.Lazy.executorServiceMap.put(namePrefix, executorServiceVirtual);
        return executorServiceVirtual;
    }

}
