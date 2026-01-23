package executor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 取消
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 09:11
 **/
public class ScheduledFuture {

    protected final AtomicBoolean cancel;

    protected ScheduledFuture() {
        this.cancel = new AtomicBoolean(false);
    }

    public void cancel() {
        cancel.set(true);
    }

}
