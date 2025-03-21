package wxdgaming.boot2.core.cache2;

import java.util.concurrent.locks.ReentrantReadWriteLock;

class CacheLock {

    final ReentrantReadWriteLock lock;
    final ReentrantReadWriteLock.ReadLock readLock;
    final ReentrantReadWriteLock.WriteLock writeLock;

    protected CacheLock() {
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

}
