package wxdgaming.boot2.core.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;

/**
 * 线程池工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-27 09:23
 **/
@Slf4j
public class ExecutorFactory {

    private static class Lazy {

        private static final Timer TIMER;
        private static AbstractExecutorService executorServiceBase;
        private static AbstractExecutorService executorServiceLogic;
        private static AbstractExecutorService executorServiceVirtual;

        static {
            TIMER = new Timer("ExecutorFactory-Timer");

        }

        public static AbstractExecutorService getExecutorServiceBase() {
            if (executorServiceBase == null) {
                executorServiceBase = new ExecutorServicePlatform(
                        "base",
                        4, 5000,
                        QueuePolicyConst.AbortPolicy
                );
            }
            return executorServiceBase;
        }

        public static AbstractExecutorService getExecutorServiceLogic() {
            if (executorServiceLogic == null) {
                executorServiceLogic = new ExecutorServicePlatform(
                        "logic",
                        16, 5000,
                        QueuePolicyConst.AbortPolicy
                );
            }
            return executorServiceLogic;
        }

        public static AbstractExecutorService getExecutorServiceVirtual() {
            if (executorServiceVirtual == null) {
                executorServiceVirtual = new ExecutorServiceVirtual(
                        "virtual",
                        32, 5000,
                        QueuePolicyConst.AbortPolicy
                );
            }
            return executorServiceVirtual;
        }
    }

    public static void init(ExecutorProperties executorProperties) {
        log.debug("初始化线程池... base {}", executorProperties.getBasic());
        Lazy.executorServiceBase = new ExecutorServicePlatform(
                "base",
                executorProperties.getBasic()
        );
        log.debug("初始化线程池... logic {}", executorProperties.getLogic());
        Lazy.executorServiceLogic = new ExecutorServicePlatform(
                "logic",
                executorProperties.getLogic()
        );
        log.debug("初始化线程池... virtual {}", executorProperties.getVirtual());
        Lazy.executorServiceLogic = new ExecutorServicePlatform(
                "virtual",
                executorProperties.getVirtual()
        );
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

    public static AbstractExecutorService create(String name, ExecutorConfig executorConfig) {
        return new ExecutorServicePlatform(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    public static ExecutorServicePlatform createPlatform(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        return new ExecutorServicePlatform(namePrefix, threadSize, queueSize, queuePolicy);
    }

    public static ExecutorServiceVirtual createVirtual(String namePrefix, int threadSize, int queueSize, QueuePolicyConst queuePolicy) {
        return new ExecutorServiceVirtual(namePrefix, threadSize, queueSize, queuePolicy);
    }

}
