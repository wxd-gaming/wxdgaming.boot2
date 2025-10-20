package wxdgaming.boot2.core.locks;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 监控
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-17 11:14
 */
public class Monitor {

    @JSONField(serialize = false, deserialize = false)
    protected transient final ReentrantLock reentrantLock = new ReentrantLock();

    public void lock() {
        reentrantLock.lock();
    }

    public void unlock() {
        reentrantLock.unlock();
    }

    public void sync(Runnable runnable) {
        lock();
        try {
            runnable.run();
        } finally {
            unlock();
        }
    }

    public <R> R supplier(Supplier<R> supplier) {
        lock();
        try {
            return supplier.get();
        } finally {
            unlock();
        }
    }

}
