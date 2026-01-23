package executor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.concurrent.Executor;

/**
 * 执行器视图
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-22 14:54
 **/
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
@ToString
public final class ExecutorVO {

    private static final ThreadLocal<ExecutorVO> local = ThreadLocal.withInitial(ExecutorVO::new);

    public static ExecutorVO threadLocal() {
        return local.get();
    }

    public static void cleanup() {
        local.remove();
    }

    private Executor executor;
    private ExecutorQueue executorQueue;

    public String queueName() {
        if (executorQueue != null) {
            return executorQueue.getQueueName();
        }
        return null;
    }

    void execute(Runnable command) {
        if (executorQueue != null) {
            executorQueue.execute(command);
        } else {
            executor.execute(command);
        }
    }

}
