package executor4;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.executor.StackUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 带当前线程回调
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-24 09:34
 **/
public class RunnableFuture extends CompletableFuture<Object> implements Runnable {

    final Executor currentExecutor;
    private final Runnable task;
    private String stack;

    public RunnableFuture(Runnable task) {
        this(task, true);
    }

    /**
     * 创建一个能回到当前线程的异步任务
     *
     * @param task      需要执行任务
     * @param initStack 如果你的堆栈信息需要重建，填false
     */
    public RunnableFuture(Runnable task, boolean initStack) {
        this.task = task;
        currentExecutor = ExecutorFactory.currentExecutor();
        if (initStack) {
            this.initStackTrace(1, 1);
        }
    }

    /**
     * 初始化 new 实例的地方
     *
     * @param initSkip 跳过构造函数的次数
     * @param skip     跳过最开始的次数
     */
    public void initStackTrace(int initSkip, int skip) {
        stack = StackUtils.stack(initSkip, skip);
    }

    @Override
    public void run() {
        try {
            task.run();
            currentExecutor.execute(() -> this.complete(null));
        } catch (Exception e) {
            currentExecutor.execute(() -> this.completeExceptionally(e));
        }
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(stack)) {
            return String.valueOf(task);
        }
        return stack + "->" + String.valueOf(task);
    }

}
