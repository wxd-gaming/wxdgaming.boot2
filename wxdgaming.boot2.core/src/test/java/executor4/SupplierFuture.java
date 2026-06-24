package executor4;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.executor.StackUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 带当前线程回调
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-24 09:34
 **/
public class SupplierFuture<U> extends CompletableFuture<U> implements Runnable {

    final Executor currentExecutor;
    private final Supplier<U> task;
    private String stack;

    public SupplierFuture(Supplier<U> task) {
        this(task, true);
    }

    /**
     * 构建带有回调的任务
     *
     * @param task      待执行的任务
     * @param initStack false不构建new的堆栈信息
     */
    public SupplierFuture(Supplier<U> task, boolean initStack) {
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
            U u = task.get();
            currentExecutor.execute(() -> this.complete(u));
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
