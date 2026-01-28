package wxdgaming.boot2.core.executor;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 事件执行器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 14:06
 **/
@Getter
@Setter
public abstract class AbstractEventRunnable implements Runnable, RunnableQueue, RunnableWarnTime {

    protected String sourceLine;
    protected String queueName = null;

    public AbstractEventRunnable() {
        this.sourceLine = StackUtils.stack(1, 1);
    }

    /** 不记录耗时统计 */
    public boolean isIgnoreRunTimeRecord() {return false;}


    @Override public String toString() {
        return getSourceLine();
    }

    @Override public void run() {
        try {
            onEvent();
        } catch (Exception e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    public abstract void onEvent() throws Exception;

}
