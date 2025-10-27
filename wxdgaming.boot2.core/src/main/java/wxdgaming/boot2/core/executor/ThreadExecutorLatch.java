package wxdgaming.boot2.core.executor;

import wxdgaming.boot2.core.util.AssertUtil;

import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * 当前线程
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-27 10:19
 **/
public class ThreadExecutorLatch {

    private static final ThreadLocal<ExecutorLatch> THREAD_LOCAL = new ThreadLocal<>();

    public static ExecutorLatch threadLocalInit() {
        return threadLocalInit(null);
    }

    public static ExecutorLatch threadLocalInit(Executor executor) {
        ExecutorLatch executorLatch = THREAD_LOCAL.get();
        AssertUtil.notNull(executorLatch, "上次的资源未释放检查代码问题");
        executorLatch = new ExecutorLatch();
        THREAD_LOCAL.set(executorLatch);
        return executorLatch;
    }

    /** 获取当前线程变量 */
    public static Optional<ExecutorLatch> opt() {
        return Optional.ofNullable(THREAD_LOCAL.get());
    }

    /** 获取当前线程变量 */
    public static ExecutorLatch get() {
        return THREAD_LOCAL.get();
    }

    public static void executor(Runnable runnable) {
        THREAD_LOCAL.get().executor(runnable);
    }

    public static void executor(Runnable runnable, Executor targetExecutor) {
        THREAD_LOCAL.get().executor(runnable, targetExecutor);
    }

    /** 获取当前线程变量 */
    public static void await() throws InterruptedException {
        THREAD_LOCAL.get().await();
    }

    /** 释放当前线程变量 */
    public static void release() {
        THREAD_LOCAL.remove();
    }

}
