package executor4;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.lang.Tick;
import wxdgaming.boot2.core.runtime.RunTimeCost;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 驱动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 19:02
 **/
@Slf4j
public class ExecutorDriver implements Executor, Runnable {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final String driverName;
    private final ExecutorDriverService executorDriverService;
    private final FixedCapacityCircularQueue<Runnable> queue;
    /** 是否已经添加到队列中 */
    private boolean addExecuting;

    public ExecutorDriver(String driverName, ExecutorDriverService executorDriverService) {
        this(driverName, executorDriverService, 1024);
    }

    public ExecutorDriver(String driverName, ExecutorDriverService executorDriverService, int capacity) {
        this.driverName = driverName;
        this.queue = new FixedCapacityCircularQueue<>(capacity);
        this.executorDriverService = executorDriverService;
    }

    public CompletableFuture<?> submit(Runnable task) {
        Executor currentExecutor = ExecutorFactory.currentExecutor();
        CompletableFuture<?> future = new CompletableFuture<>();
        RunnableWrapper runnable = new RunnableWrapper(() -> {
            try {
                task.run();
                currentExecutor.execute(() -> future.complete(null));
            } catch (Exception e) {
                currentExecutor.execute(() -> future.completeExceptionally(e));
            }
        }, false);
        execute(runnable);
        return future;
    }

    public <U> CompletableFuture<U> submit(Supplier<U> task) {
        Executor currentExecutor = ExecutorFactory.currentExecutor();
        CompletableFuture<U> future = new CompletableFuture<>();
        RunnableWrapper runnable = new RunnableWrapper(() -> {
            try {
                U u = task.get();
                currentExecutor.execute(() -> future.complete(u));
            } catch (Exception e) {
                currentExecutor.execute(() -> future.completeExceptionally(e));
            }
        }, false);
        runnable.initStackTrace(0, 1);
        execute(runnable);
        return future;
    }

    public FutureProxy schedule(Runnable command, long delay, TimeUnit unit) {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper(ScheduleType.Scheduled, command, 0, delay, unit);
        scheduleWrapper.nextSchedule();
        return scheduleWrapper;
    }

    public FutureProxy scheduleAtFixedRate(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper(ScheduleType.ScheduleAt, command, initialDelay, delay, unit);
        scheduleWrapper.nextSchedule();
        return scheduleWrapper;
    }

    public FutureProxy scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduleWrapper scheduleWrapper = new ScheduleWrapper(ScheduleType.ScheduleWith, command, initialDelay, delay, unit);
        scheduleWrapper.nextSchedule();
        return scheduleWrapper;
    }

    @Override
    public void execute(Runnable command) {
        reentrantLock.lock();
        try {
            queue.offer(command);
            if (!addExecuting) {
                addExecuting = true;
                executorDriverService.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void run() {
        Runnable poll;
        reentrantLock.lock();
        try {
            poll = queue.poll();
        } finally {
            reentrantLock.unlock();
        }
        try {
            if (poll != null) {
                Thread currentThread = Thread.currentThread();
                ExecutorFactory.__currentExecutorMap.put(currentThread, this);
                long start = System.nanoTime();
                try {
                    poll.run();
                    long costMs = RunTimeCost.costMs(start);
                    if (costMs > 50) {
                        log.warn("执行任务:{}, 耗时：{}", poll.toString(), costMs);
                    }
                } finally {
                    ExecutorFactory.__currentExecutorMap.remove(currentThread);
                }
            }
        } finally {
            reentrantLock.lock();
            try {
                if (!queue.isEmpty()) {
                    executorDriverService.execute(this);
                } else {
                    addExecuting = false;
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return "ExecutorDriver{driverName='%s'}".formatted(driverName);
    }

    enum ScheduleType {
        ScheduleAt,
        ScheduleWith,
        Scheduled,
    }

    class ScheduleWrapper implements Runnable, FutureProxy {

        final ScheduleType scheduleType;
        final RunnableWrapper runnableWrapper;
        final long initialDelay;
        final long delay;
        final TimeUnit unit;
        volatile boolean first = true;
        volatile ScheduledFuture<?> schedule;

        public ScheduleWrapper(ScheduleType scheduleType, Runnable command, long initialDelay, long delay, TimeUnit unit) {
            this.scheduleType = scheduleType;
            this.runnableWrapper = new RunnableWrapper(command, false) {
                @Override
                public void afterExecute(Runnable task, Throwable throwable) {
                    if (ScheduleWrapper.this.scheduleType == ScheduleType.ScheduleWith) {
                        ScheduleWrapper.this.nextSchedule();
                    }
                }
            };
            this.runnableWrapper.initStackTrace(0, 3);
            this.initialDelay = initialDelay;
            this.delay = delay;
            this.unit = unit;
        }

        public void nextSchedule() {
            switch (scheduleType) {
                case ScheduleAt -> {
                    if (first) {
                        first = false;
                    }
                    schedule = ExecutorDriver.this.executorDriverService.scheduleAtFixedRate(this, initialDelay, delay, unit);
                }
                case ScheduleWith -> {
                    if (first) {
                        first = false;
                        schedule = ExecutorDriver.this.executorDriverService.schedule(this, initialDelay, unit);
                    } else {
                        schedule = ExecutorDriver.this.executorDriverService.schedule(this, delay, unit);
                    }
                }
                case Scheduled -> {
                    if (!first) {
                        return;
                    }
                    first = false;
                    schedule = ExecutorDriver.this.executorDriverService.schedule(this, delay, unit);
                }
            }
        }

        @Override
        public void cancel() {
            if (schedule != null) {
                schedule.cancel(true);
            }
        }

        @Override
        public void run() {
            ExecutorDriver.this.execute(runnableWrapper);
        }

    }

}
