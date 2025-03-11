package wxdgaming.boot2.core.threading;

import ch.qos.logback.core.LogbackUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.boot2.core.BootConfig;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.function.ConsumerE0;
import wxdgaming.boot2.core.lang.Tick;
import wxdgaming.boot2.core.shutdown;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.GlobalUtil;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认线程池
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-09-28 14:22
 **/
@Getter
public final class ExecutorUtil implements Serializable {

    @Getter private static ExecutorUtil instance = new ExecutorUtil();

    public static void resetInstance() {
        executorIgnoredException(instance.TIMER_THREAD::clear);
        executorIgnoredException(instance.TIMER_THREAD::interrupt);
        executorIgnoredException(instance.TIMER_THREAD::join);
        executorIgnoredException(instance.GUARD_THREAD::interrupt);
        executorIgnoredException(instance.GUARD_THREAD::join);
        instance.All_THREAD_LOCAL.values().forEach(executorServices -> {
            System.out.println("shutdown executorServices start: " + executorServices.getName());
            executorIgnoredException(executorServices::terminate);
            System.out.println("shutdown executorServices end: " + executorServices.getName());
        });
        instance.All_THREAD_LOCAL.clear();
        instance = new ExecutorUtil();
    }

    /** 忽略异常 */
    public static void executorIgnoredException(ConsumerE0 ce) {
        try {
            ce.accept();
        } catch (Throwable ignored) {}
    }

    /** 定时任务线程 */
    final TimerThread TIMER_THREAD = new TimerThread();
    /** 守护线程 */
    final GuardThread GUARD_THREAD = new GuardThread();
    /** 当前线程 */
    final ThreadLocal<ExecutorServiceJob> CurrentThread = new ThreadLocal<>();
    /** 当前正在执行的任务 */
    final ConcurrentHashMap<Thread, ExecutorServiceJob> Run_THREAD_LOCAL = new ConcurrentHashMap<>();
    /** 全部初始化的 */
    public final ConcurrentHashMap<String, IExecutorServices> All_THREAD_LOCAL = new ConcurrentHashMap<>();
    /** 属于后台线程池, 默认线程池， 一旦收到停服新号，线程立马关闭了 */
    private IExecutorServices defaultExecutor = null;
    /** 属于后台线程池, 逻辑线程池，一旦收到停服新号，线程立马关闭了 */
    private IExecutorServices logicExecutor = null;
    /** 属于后台线程池, 虚拟线程池，一旦收到停服新号，线程立马关闭了 */
    private IExecutorServices virtualExecutor = null;

    public void init() {
        Logger logger = LogbackUtil.logger();
        ExecutorConfig defaultConfig = BootConfig.getIns().defaultConfig();
        if (logger.isDebugEnabled()) {
            logger.debug("ExecutorUtil init default config: {}", defaultConfig.toJsonString());
        }
        defaultExecutor = newExecutorServices("default-executor", defaultConfig.getCoreSize(), defaultConfig.getMaxSize(), defaultConfig.getMaxQueueSize());
        ExecutorConfig logicConfig = BootConfig.getIns().logicConfig();

        if (logger.isDebugEnabled()) {
            logger.debug("ExecutorUtil init logic config: {}", defaultConfig.toJsonString());
        }

        logicExecutor = newExecutorServices("logic-executor", logicConfig.getCoreSize(), logicConfig.getMaxSize(), logicConfig.getMaxQueueSize());
        ExecutorConfig virtualConfig = BootConfig.getIns().virtualConfig();

        if (logger.isDebugEnabled()) {
            logger.debug("ExecutorUtil init virtual config: {}", defaultConfig.toJsonString());
        }

        virtualExecutor = newExecutorVirtualServices("virtual-executor", virtualConfig.getCoreSize(), virtualConfig.getMaxSize(), virtualConfig.getMaxQueueSize());

        TIMER_THREAD.start();
        GUARD_THREAD.start();

    }

    @shutdown
    @Sort(1001)
    public void shutdown() {
        resetInstance();
    }

    /**
     * 默认队列最大长度2000,单线程
     *
     * @param name 线程池名称
     * @return
     */
    public ExecutorServices newExecutorServices(String name) {
        return newExecutorServices(name, false);
    }

    public ExecutorServices newExecutorServices(String name, boolean daemon) {
        return newExecutorServices(name, daemon, 1, 1, 5000);
    }

    /**
     * 线程池核心数量和最大数量相等，
     *
     * @param name     线程池名称
     * @param coreSize 线程核心数量
     * @param maxSize  线程最大数量
     * @return
     */
    public ExecutorServices newExecutorServices(String name, int coreSize, int maxSize, int queueCheckSize) {
        return newExecutorServices(name, false, coreSize, maxSize, queueCheckSize);
    }

    /**
     * @param name     线程池名称
     * @param daemon   守护线程状态
     * @param coreSize 线程核心数量
     * @param maxSize  线程最大数量
     * @return
     */
    public ExecutorServices newExecutorServices(String name, boolean daemon, int coreSize, int maxSize, int queueCheckSize) {
        return new ExecutorServices(name, daemon, coreSize, maxSize, queueCheckSize);
    }


    /**
     * 虚拟线程池 默认队列最大长度2000,单线程
     * <p>
     * 禁止使用 synchronized 同步锁
     * <p>
     * 直接线程池，每一个任务都会new Virtual Thread
     *
     * @param name 线程池名称
     * @return
     */
    public ExecutorVirtualServices newExecutorVirtualServices(String name, int queueCheckSize) {
        return newExecutorVirtualServices(name, 1, 1, queueCheckSize);
    }

    /**
     * 虚拟线程池
     * <p>
     * 禁止使用 synchronized 同步锁
     * <p>
     * 直接线程池，每一个任务都会new Virtual Thread
     *
     * @param name     线程池名称
     * @param coreSize 线程核心数量
     * @param coreSize 线程最大数量
     * @return
     */
    public ExecutorVirtualServices newExecutorVirtualServices(String name, int coreSize, int maxSize, int queueCheckSize) {
        return new ExecutorVirtualServices(name, coreSize, maxSize, queueCheckSize);
    }

    /** 检测当前线程是否是同一线程 */
    public boolean checkCurrentThread(String queueKey) {
        return Objects.equals(currentThreadQueueKey(), queueKey);
    }

    /** 当前线程队列名称 */
    public String currentThreadQueueKey() {
        return Optional.ofNullable(CurrentThread.get()).map(s -> s.queueName).orElse("");
    }

    ExecutorUtil() {}

    /** 守护线程 */
    protected class GuardThread extends Thread implements Serializable {

        protected GuardThread() {
            super("guard-thread");
            setPriority(Thread.MIN_PRIORITY);
        }

        @Override public void run() {
            Tick tick = new Tick(50, 10, TimeUnit.SECONDS);
            Logger logger = LoggerFactory.getLogger(this.getClass());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    try {
                        tick.waitNext();
                        StringBuilder stringBuilder = new StringBuilder().append("\n");
                        for (ExecutorServiceJob serviceJob : Run_THREAD_LOCAL.values()) {
                            serviceJob.check(stringBuilder);
                        }
                        if (stringBuilder.length() > 4) {
                            LoggerFactory.getLogger(this.getClass()).info(stringBuilder.toString());
                        }
                    } catch (Throwable throwable) {
                        if (!(throwable instanceof InterruptedException))
                            GlobalUtil.exception("guard-thread", throwable);
                    }
                } catch (Throwable throwable) {/*不能加东西，log也有可能异常*/}
            }
            logger.info("线程 {} 退出", Thread.currentThread());
        }
    }

    protected class TimerThread extends Thread {

        final ReentrantLock relock = new ReentrantLock();
        private LinkedList<TimerJob> timerJobs = new LinkedList<>();

        public TimerThread() {
            super("timer-executor");
            setPriority(6);
        }

        public void add(TimerJob timerJob) {
            relock.lock();
            try {
                timerJobs.add(timerJob);
                timerJobs.sort(Comparator.comparingLong(TimerJob::getLastExecTime));
            } finally {
                relock.unlock();
            }
        }

        public void clear() {
            relock.lock();
            try {
                timerJobs = new LinkedList<>();
            } finally {
                relock.unlock();
            }
        }

        @Override public void run() {
            Tick tick = new Tick(1, 2, TimeUnit.MILLISECONDS);
            Logger logger = LoggerFactory.getLogger(this.getClass());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    tick.waitNext();
                    relock.lock();
                    long millis = MyClock.millis();
                    try {
                        boolean needSort = false;
                        Iterator<TimerJob> iterator = timerJobs.iterator();
                        while (iterator.hasNext()) {
                            try {
                                TimerJob next = iterator.next();
                                if (next.IExecutorServices.isShutdown() || next.IExecutorServices.isTerminated()) {
                                    /*线程正在关闭不处理*/
                                    iterator.remove();
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("线程{}正在关闭不处理{}", next.IExecutorServices.getName(), next.executorServiceJob.toString());
                                    }
                                    continue;
                                }
                                if (next.checkRunTime(millis)) {
                                    if (next.runJob()) {
                                        if (next.isOver()) {
                                            /*移除当前对象*/
                                            iterator.remove();
                                            if (logger.isDebugEnabled()) {
                                                logger.debug("线程{}执行时间到期，移除{}", next.IExecutorServices.getName(), next.executorServiceJob.toString());
                                            }
                                        } else {
                                            needSort = true;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            } catch (Throwable throwable) {
                                GlobalUtil.exception("定时任务公共处理器", throwable);
                            }
                        }
                        if (needSort) {
                            timerJobs.sort(Comparator.comparingLong(TimerJob::getLastExecTime));
                        }
                    } finally {
                        relock.unlock();
                    }
                } catch (Throwable throwable) {/*不能加东西，log也有可能异常*/}
            }
            logger.info("线程 {} 退出", Thread.currentThread());
        }
    }

}
