package wxdgaming.boot2.core.executor;

import wxdgaming.boot2.core.BootConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程执行器工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 11:20
 **/
public class ExecutorFactory {

    private static final AtomicBoolean InitEnd = new AtomicBoolean();
    private static ExecutorMonitor EXECUTOR_MONITOR;
    private static ScheduledExecutorService scheduledExecutorService;
    private static ConcurrentHashMap<String, ExecutorService> EXECUTOR_MAP;

    private static ExecutorService EXECUTOR_SERVICE_BASIC;
    private static ExecutorService EXECUTOR_SERVICE_LOGIC;
    private static ExecutorService EXECUTOR_SERVICE_VIRTUAL;

    static void check() {
        if (!InitEnd.get() && InitEnd.compareAndSet(false, true)) {
            init();
            InitEnd.set(true);
        }
    }

    static void init() {
        EXECUTOR_MAP = new ConcurrentHashMap<>();
        EXECUTOR_MONITOR = new ExecutorMonitor();
        scheduledExecutorService = newSingleThreadScheduledExecutor("scheduled");
        BootConfig bootConfig = BootConfig.getIns();
        EXECUTOR_SERVICE_BASIC = create("basic", bootConfig.basicConfig());
        EXECUTOR_SERVICE_LOGIC = create("logic", bootConfig.logicConfig());
        EXECUTOR_SERVICE_VIRTUAL = createVirtual("virtual", bootConfig.virtualConfig());
    }

    public static ExecutorService getExecutor(String name) {
        return getExecutorMap().get(name);
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new NameThreadFactory(name, true));
    }

    public static ExecutorServicePlatform create(String name, ExecutorConfig executorConfig) {
        return create(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个平台线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServicePlatform create(String name, int corePoolSize) {
        return create(name, corePoolSize, 5000, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize, int queueSize, QueuePolicy queuePolicy) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(name, corePoolSize, queueSize, queuePolicy);
        getExecutorMap().put(name, executorServicePlatform);
        return executorServicePlatform;
    }

    public static ExecutorServiceVirtual createVirtual(String name, ExecutorConfig executorConfig) {
        return createVirtual(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个虚拟线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize) {
        return createVirtual(name, corePoolSize, 5000, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize, int queueSize, QueuePolicy queuePolicy) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(name, corePoolSize, queueSize, queuePolicy);
        getExecutorMap().put(name, executorServiceVirtual);
        return executorServiceVirtual;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        check();
        return scheduledExecutorService;
    }

    public static ConcurrentHashMap<String, ExecutorService> getExecutorMap() {
        check();
        return EXECUTOR_MAP;
    }

    public static ExecutorService getExecutorServiceBasic() {
        check();
        return EXECUTOR_SERVICE_BASIC;
    }

    public static ExecutorService getExecutorServiceLogic() {
        check();
        return EXECUTOR_SERVICE_LOGIC;
    }

    public static ExecutorService getExecutorServiceVirtual() {
        check();
        return EXECUTOR_SERVICE_VIRTUAL;
    }

    public static ExecutorMonitor getExecutorMonitor() {
        return EXECUTOR_MONITOR;
    }
}
