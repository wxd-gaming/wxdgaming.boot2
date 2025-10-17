package wxdgaming.boot2.core.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * 监控
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-17 11:14
 */
public class MonitorReadWrite {

    final ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock readLock = reentrantLock.readLock();
    final ReentrantReadWriteLock.WriteLock writeLock = reentrantLock.writeLock();

    public void readLock() {
        readLock.lock();
    }

    public void unReadLock() {
        readLock.unlock();
    }

    public void writeLock() {
        writeLock.lock();
    }

    public void unWriteLock() {
        writeLock.unlock();
    }

    public void syncRead(Runnable runnable) {
        readLock();
        try {
            runnable.run();
        } finally {
            unReadLock();
        }
    }

    public <R> R supplierRead(Supplier<R> supplier) {
        readLock();
        try {
            return supplier.get();
        } finally {
            unReadLock();
        }
    }

    /** 写入锁 */
    public void syncWrite(Runnable runnable) {
        writeLock();
        try {
            runnable.run();
        } finally {
            unWriteLock();
        }
    }

    /** 写入锁 */
    public <R> R supplierWrite(Supplier<R> supplier) {
        writeLock();
        try {
            return supplier.get();
        } finally {
            unWriteLock();
        }
    }

}
