package wxdgaming.boot2.core.threading;

import ch.qos.logback.core.LogbackUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.boot2.core.lang.Tick;
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
public final class ExecutorUtil implements Serializable {

    /** 定时任务线程 */
    static final TimerThread TIMER_THREAD = new TimerThread();
    /** 守护线程 */
    static final GuardThread GUARD_THREAD = new GuardThread();
    /** 当前线程 */
    static final ThreadLocal<ExecutorServiceJob> CurrentThread = new ThreadLocal<>();
    /** 当前正在执行的任务 */
    static final ConcurrentHashMap<Thread, ExecutorServiceJob> Run_THREAD_LOCAL = new ConcurrentHashMap<>();
    /** 全部初始化的 */
    public static final ConcurrentHashMap<String, IExecutorServices> All_THREAD_LOCAL = new ConcurrentHashMap<>();
    /** 属于后台线程池, 默认线程池， 一旦收到停服新号，线程立马关闭了 */
    @Getter private static IExecutorServices defaultExecutor = null;
    /** 属于后台线程池, 逻辑线程池，一旦收到停服新号，线程立马关闭了 */
    @Getter private static IExecutorServices logicExecutor = null;
    /** 属于后台线程池, 虚拟线程池，一旦收到停服新号，线程立马关闭了 */
    @Getter private static IExecutorServices virtualExecutor = null;

    public static void init(ExecutorConfig config) {
        Logger logger = LogbackUtil.logger();
        if (logger.isDebugEnabled()) {
            logger.debug("ExecutorUtil init config: {}", config.toJsonString());
        }
        defaultExecutor = newExecutorServices("default-executor", config.getDefaultCoreSize(), config.getDefaultMaxSize());
        logicExecutor = newExecutorServices("logic-executor", config.getLogicCoreSize(), config.getLogicMaxSize());
        virtualExecutor = newExecutorVirtualServices("virtual-executor", config.getVirtualCoreSize(), config.getVirtualMaxSize());
    }

    /**
     * 默认队列最大长度2000,单线程
     *
     * @param name 线程池名称
     * @return
     */
    public static ExecutorServices newExecutorServices(String name) {
        return newExecutorServices(name, false);
    }

    public static ExecutorServices newExecutorServices(String name, boolean daemon) {
        return newExecutorServices(name, daemon, 1, 1);
    }

    /**
     * 默认队列最大长度 Integer.MAX_VALUE
     *
     * @param name     线程池名称
     * @param coreSize 线程最大数量
     * @return
     */
    public static ExecutorServices newExecutorServices(String name, int coreSize) {
        return newExecutorServices(name, false, coreSize);
    }

    /**
     * @param name     线程池名称
     * @param coreSize 线程最大数量
     * @return
     */
    public static ExecutorServices newExecutorServices(String name, boolean daemon, int coreSize) {
        return newExecutorServices(name, daemon, coreSize, coreSize);
    }

    /**
     * 线程池核心数量和最大数量相等，
     *
     * @param name     线程池名称
     * @param coreSize 线程核心数量
     * @param maxSize  线程最大数量
     * @return
     */
    public static ExecutorServices newExecutorServices(String name, int coreSize, int maxSize) {
        return newExecutorServices(name, false, coreSize, maxSize);
    }

    /**
     * @param name     线程池名称
     * @param daemon   守护线程状态
     * @param coreSize 线程核心数量
     * @param maxSize  线程最大数量
     * @return
     */
    public static ExecutorServices newExecutorServices(String name, boolean daemon, int coreSize, int maxSize) {
        return new ExecutorServices(name, daemon, coreSize, maxSize);
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
    public static ExecutorVirtualServices newExecutorVirtualServices(String name) {
        return newExecutorVirtualServices(name, 1);
    }

    /**
     * 虚拟线程池核心数量和最大数量相等，
     * <p>
     * 禁止使用 synchronized 同步锁
     * <p>
     * 直接线程池，每一个任务都会new Virtual Thread
     *
     * @param name     线程池名称
     * @param coreSize 线程核心数量
     * @return
     */
    public static ExecutorVirtualServices newExecutorVirtualServices(String name, int coreSize) {
        return newExecutorVirtualServices(name, coreSize, coreSize);
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
    public static ExecutorVirtualServices newExecutorVirtualServices(String name, int coreSize, int maxSize) {
        return new ExecutorVirtualServices(name, coreSize, maxSize);
    }

    /** 检测当前线程是否是同一线程 */
    public static boolean checkCurrentThread(String queueKey) {
        return Objects.equals(currentThreadQueueKey(), queueKey);
    }

    /** 当前线程队列名称 */
    public static String currentThreadQueueKey() {
        return Optional.ofNullable(CurrentThread.get()).map(s -> s.queueName).orElse("");
    }

    ExecutorUtil() {}

    /** 守护线程 */
    protected static class GuardThread extends Thread implements Serializable {

        protected GuardThread() {
            super("guard-thread");
            setPriority(Thread.MIN_PRIORITY);
            start();
        }

        @Override public void run() {
            Tick tick = new Tick(50, 1, TimeUnit.SECONDS);
            while (!GlobalUtil.SHUTTING.get()) {
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
                        GlobalUtil.exception("guard-thread", throwable);
                    }
                } catch (Throwable throwable) {/*不能加东西，log也有可能异常*/}
            }
            LoggerFactory.getLogger(this.getClass()).info("guard-thread 线程退出");
        }
    }

    protected static class TimerThread extends Thread {

        final ReentrantLock relock = new ReentrantLock();
        private LinkedList<TimerJob> timerJobs = new LinkedList<>();

        public TimerThread() {
            super("timer-executor");
            setPriority(6);
            start();
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

        @Override public void run() {
            Tick tick = new Tick(1, 2, TimeUnit.MILLISECONDS);
            Logger logger = LoggerFactory.getLogger(this.getClass());
            while (!GlobalUtil.SHUTTING.get()) {
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
                                        /*优先移除当前对象*/
                                        if (next.isOver()) {
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
            logger.info("定时任务公共处理器 线程退出");
        }
    }

}
