package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.reflect.AnnUtil;

import java.lang.reflect.Method;

/**
 * 事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 19:56
 **/
@Slf4j
@Getter
@Setter
public abstract class ExecutorEvent extends ExecutorJob implements IExecutorQueue {

    protected ExecutorWith executorWith;
    protected String queueName;

    public ExecutorEvent() {
        super(null);
        this.stack = StackUtils.stack(0, 1);
    }

    public ExecutorEvent(Method method) {
        super(null);
        this.stack = StackUtils.stack(0, 1);
        this.executorWith = AnnUtil.ann(method, ExecutorWith.class);
        ExecutorLog executorLog1 = AnnUtil.ann(method, ExecutorLog.class);
        if (executorLog1 != null) {
            this.executorLog = executorLog1;
        }
        this.queueName = this.executorWith == null ? null : this.executorWith.queueName();
    }

    /** 堆栈，也是任务名称，日志记录关键 */
    @Override public String getStack() {
        return super.getStack();
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            onEvent();
        } catch (Throwable throwable) {
            log.error("{}", getStack(), throwable);
        } finally {
            this.threadContext = null;
            ThreadContext.cleanup();
            ExecutorMonitor.release();
            runAfter();
        }
    }

    public abstract void onEvent() throws Exception;

    public void submit() {
        ExecutorService executorService = ExecutorFactory.getExecutorServiceLogic();
        if (getExecutorWith() != null) {
            String threadName = getExecutorWith().threadName();
            if (StringUtils.isNotBlank(threadName)) {
                executorService = ExecutorFactory.getExecutor(threadName);
            } else if (getExecutorWith().useVirtualThread()) {
                executorService = ExecutorFactory.getExecutorServiceVirtual();
            }
        }
        executorService.execute(this);
    }

}
