package wxdgaming.boot2.core.executor;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.runtime.RunTimeUtil;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程执行器工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 11:20
 **/
@Order(value = Integer.MIN_VALUE)
@Configuration
public class ExecutorFactory implements InitPrint {

    public static class Lazy {
        public static ExecutorFactory instance = null;
    }

    @Getter private final ExecutorMonitor EXECUTOR_MONITOR;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentHashMap<String, ExecutorService> EXECUTOR_MAP;

    private final ExecutorService EXECUTOR_SERVICE_BASIC;
    private final ExecutorService EXECUTOR_SERVICE_LOGIC;
    private final ExecutorService EXECUTOR_SERVICE_VIRTUAL;

    public ExecutorFactory(ExecutorProperties executorProperties) {
        AssertUtil.isTrue(Lazy.instance == null, "ExecutorFactory is already exists");
        Lazy.instance = this;
        EXECUTOR_MAP = new ConcurrentHashMap<>();
        EXECUTOR_MONITOR = new ExecutorMonitor();
        scheduledExecutorService = newSingleThreadScheduledExecutor("core.scheduled");
        EXECUTOR_SERVICE_BASIC = create("basic", executorProperties.getBasic());
        EXECUTOR_SERVICE_LOGIC = create("logic", executorProperties.getLogic());
        EXECUTOR_SERVICE_VIRTUAL = createVirtual("virtual", executorProperties.getVirtual());
        if (executorProperties.getOutRunTimeDelay() > 0) {
            RunTimeUtil.openRecord(executorProperties.getOutRunTimeDelay());
        }
    }

    public static ExecutorService getExecutor(String name) {
        return getExecutorMap().get(name);
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new NameThreadFactory(name, true));
    }

    public static ExecutorServicePlatform create(String name, ExecutorConfig executorConfig) {
        return create(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getWarnSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个平台线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServicePlatform create(String name, int corePoolSize) {
        return create(name, corePoolSize, 5000, 500, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize, int queueSize, int warnSize, QueuePolicy queuePolicy) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(name, corePoolSize, queueSize, warnSize, queuePolicy);
        getExecutorMap().put(name, executorServicePlatform);
        return executorServicePlatform;
    }

    public static ExecutorServiceVirtual createVirtual(String name, ExecutorConfig executorConfig) {
        return createVirtual(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getWarnSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个虚拟线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize) {
        return createVirtual(name, corePoolSize, 5000, 500, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize, int queueSize, int warnSize, QueuePolicy queuePolicy) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(name, corePoolSize, queueSize, warnSize, queuePolicy);
        getExecutorMap().put(name, executorServiceVirtual);
        return executorServiceVirtual;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return Lazy.instance.scheduledExecutorService;
    }

    public static ConcurrentHashMap<String, ExecutorService> getExecutorMap() {
        return Lazy.instance.EXECUTOR_MAP;
    }

    public static ExecutorService getExecutorServiceBasic() {
        return Lazy.instance.EXECUTOR_SERVICE_BASIC;
    }

    public static ExecutorService getExecutorServiceLogic() {
        return Lazy.instance.EXECUTOR_SERVICE_LOGIC;
    }

    /** 虚拟线程池 */
    public static ExecutorService getExecutorServiceVirtual() {
        return Lazy.instance.EXECUTOR_SERVICE_VIRTUAL;
    }

    public static ExecutorMonitor getExecutorMonitor() {
        return Lazy.instance.EXECUTOR_MONITOR;
    }
}
