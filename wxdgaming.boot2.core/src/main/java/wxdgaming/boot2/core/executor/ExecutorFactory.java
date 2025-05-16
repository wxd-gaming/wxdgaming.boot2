package wxdgaming.boot2.core.executor;

import wxdgaming.boot2.core.BootConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程执行器工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 11:20
 **/
public class ExecutorFactory {

    static final ExecutorMonitor EXECUTOR_MONITOR = new ExecutorMonitor();
    static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    static final ConcurrentHashMap<String, ExecutorService> EXECUTOR_MAP = new ConcurrentHashMap<>();

    public static ExecutorService EXECUTOR_SERVICE_BASIC;
    public static ExecutorService EXECUTOR_SERVICE_LOGIC;
    public static ExecutorService EXECUTOR_SERVICE_VIRTUAL;

    static {
        BootConfig bootConfig = BootConfig.getIns();
        EXECUTOR_SERVICE_BASIC = create("basic", bootConfig.basicConfig().getCoreSize());
        EXECUTOR_SERVICE_LOGIC = create("logic", bootConfig.logicConfig().getCoreSize());
        EXECUTOR_SERVICE_VIRTUAL = createVirtual("virtual", bootConfig.virtualConfig().getCoreSize());
    }

    public static ExecutorService getExecutor(String name) {
        return EXECUTOR_MAP.get(name);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(name, corePoolSize);
        EXECUTOR_MAP.put(name, executorServicePlatform);
        return executorServicePlatform;
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(name, corePoolSize);
        EXECUTOR_MAP.put(name, executorServiceVirtual);
        return executorServiceVirtual;
    }

}
