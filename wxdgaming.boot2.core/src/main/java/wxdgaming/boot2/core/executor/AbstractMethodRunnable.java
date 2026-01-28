package wxdgaming.boot2.core.executor;

import lombok.Getter;
import wxdgaming.boot2.core.reflect.AnnUtil;
import wxdgaming.boot2.core.runtime.IgnoreRunTimeRecord;

import java.lang.reflect.Method;

/**
 * 基于反射的 method 的执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-27 10:37
 **/
@Getter
public abstract class AbstractMethodRunnable extends AbstractEventRunnable {

    final ExecutorLog executorLog;
    final ExecutorWith executorWith;
    final IgnoreRunTimeRecord ignoreRunTimeRecord;

    public AbstractMethodRunnable(Method method) {
        if (method != null) {
            this.ignoreRunTimeRecord = AnnUtil.ann(method, IgnoreRunTimeRecord.class);
            this.executorLog = AnnUtil.ann(method, ExecutorLog.class);
            this.executorWith = AnnUtil.ann(method, ExecutorWith.class);
            this.sourceLine = method.getDeclaringClass() + "#" + method.getName();
        } else {
            this.ignoreRunTimeRecord = null;
            this.executorLog = null;
            this.executorWith = null;
            this.sourceLine = StackUtils.stack(2, 1);
        }
    }

    @Override public boolean isIgnoreRunTimeRecord() {
        if (getIgnoreRunTimeRecord() != null) return true;
        return super.isIgnoreRunTimeRecord();
    }

    @Override public boolean offWarnLog() {
        if (getExecutorLog() != null) return getExecutorLog().offWarnLog();
        return super.offWarnLog();
    }

    @Override public long getSubmitWarnTime() {
        if (getExecutorLog() != null) return getExecutorLog().submitWarnTime();
        return super.getSubmitWarnTime();
    }

    @Override public long getExecutorWarnTime() {
        if (getExecutorLog() != null) return getExecutorLog().executorWarnTime();
        return super.getExecutorWarnTime();
    }

    public void submit() {
        AbstractExecutorService executorService = ExecutorFactory.getExecutorServiceLogic();
        ExecutorWith __executorWith = getExecutorWith();
        if (__executorWith != null) {
            if (__executorWith.useVirtualThread()) {
                executorService = ExecutorFactory.getExecutorServiceVirtual();
            }
        }
        executorService.execute(this);
    }

}
