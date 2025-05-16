package wxdgaming.boot2.core.executor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.reflect.AnnUtil;

import java.lang.reflect.Method;

/**
 * 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 19:56
 **/
@Slf4j
public abstract class ExecutorEvent extends ExecutorJob implements IExecutorQueue {

    protected ExecutorWith executorWith;
    @Setter protected String queueName;

    public ExecutorEvent() {
        super(null);
    }

    public ExecutorEvent(Method method) {
        super(null);
        this.executorWith = AnnUtil.ann(method, ExecutorWith.class);
        this.queueName = this.executorWith == null ? null : this.executorWith.queueName();
    }

    @Override public String getStack() {
        return super.getStack();
    }

    @Override public String queueName() {
        return queueName;
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            onEvent();
        } catch (Throwable throwable) {
            log.error("{}", getStack(), throwable);
        } finally {
            ExecutorMonitor.release();
            runAfter();
        }
    }

    public abstract void onEvent() throws Exception;

    public void submit() {
        ExecutorService executorService = ExecutorFactory.EXECUTOR_SERVICE_LOGIC;
        if (executorWith != null) {
            String threadName = executorWith.threadName();
            if (StringUtils.isNotBlank(threadName)) {
                executorService = ExecutorFactory.getExecutor(threadName);
            }
        }
        executorService.execute(this);
    }

}
