package executor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 取消状态代持
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-23 09:11
 **/
public class CancelHolding {

    private final AtomicBoolean cancel;

    protected CancelHolding() {
        this.cancel = new AtomicBoolean(false);
    }

    public boolean isCancel() {
        return cancel.get();
    }

    public void cancel() {
        cancel.set(true);
    }

}
