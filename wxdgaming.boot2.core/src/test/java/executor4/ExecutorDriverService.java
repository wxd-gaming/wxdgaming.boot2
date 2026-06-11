package executor4;

import java.util.List;
import java.util.concurrent.*;

/**
 * 驱动服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 20:42
 **/
public class ExecutorDriverService implements Executor {

    private final ScheduledExecutorService scheduledExecutorService;
    private final ConcurrentHashMap<String, ExecutorDriver> driverMap = new ConcurrentHashMap<>();

    public ExecutorDriverService(String name, int corePoolSize) {
        scheduledExecutorService = Executors.newScheduledThreadPool(
                corePoolSize,
                new ExecutorFactory.NameThreadFactory(name)
        );
    }

    /** 相当于队列模式 */
    public ExecutorDriver driver(String driverName) {
        return driverMap.computeIfAbsent(driverName, q -> new ExecutorDriver(q, ExecutorDriverService.this));
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, delay, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return scheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(command, delay, unit);
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return scheduledExecutorService.shutdownNow();
    }

    public boolean isShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    public boolean isTerminated() {
        return scheduledExecutorService.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return scheduledExecutorService.awaitTermination(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        scheduledExecutorService.execute(command);
    }

}
