package executor;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.QueuePolicyConst;

/**
 * cron 表达式服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 14:37
 **/
@Slf4j
public class CronService {

    private final ExecutorServicePlatform executorServicePlatform;

    public CronService(int threadSize) {
        this.executorServicePlatform = new ExecutorServicePlatform("cron", threadSize, 50000, QueuePolicyConst.AbortPolicy);
    }

    public CancelHolding addJob(String cronExpression, Runnable runnable) {
        CronRunnable cronRunnable = new CronRunnable(this.executorServicePlatform, CronExpressionUtil.parse(cronExpression), runnable);
        return cronRunnable.cancelHolding;
    }

}
