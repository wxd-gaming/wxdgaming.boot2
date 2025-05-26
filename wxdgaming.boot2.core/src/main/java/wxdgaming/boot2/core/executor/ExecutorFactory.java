package wxdgaming.boot2.core.executor;

import wxdgaming.boot2.core.BootConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程执行器工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 11:20
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
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        BootConfig bootConfig = BootConfig.getIns();
        EXECUTOR_SERVICE_BASIC = create("basic", bootConfig.basicConfig().getCoreSize(), bootConfig.basicConfig().getMaxQueueSize());
        EXECUTOR_SERVICE_LOGIC = create("logic", bootConfig.logicConfig().getCoreSize(), bootConfig.logicConfig().getMaxQueueSize());
        EXECUTOR_SERVICE_VIRTUAL = createVirtual("virtual", bootConfig.virtualConfig().getCoreSize(), bootConfig.virtualConfig().getMaxQueueSize());
    }

    public static ExecutorService getExecutor(String name) {
        return getExecutorMap().get(name);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize) {
        return create(name, corePoolSize, Integer.MAX_VALUE);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize, int queueSize) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(name, corePoolSize, queueSize);
        getExecutorMap().put(name, executorServicePlatform);
        return executorServicePlatform;
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize) {
        return createVirtual(name, corePoolSize, Integer.MAX_VALUE);
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize, int queueSize) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(name, corePoolSize, queueSize);
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
}
