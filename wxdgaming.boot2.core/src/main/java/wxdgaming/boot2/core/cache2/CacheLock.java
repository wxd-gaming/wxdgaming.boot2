package wxdgaming.boot2.core.cache2;

import lombok.Getter;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 重入锁,单例锁
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-14 13:22
 */
@Getter
public class CacheLock {

    final ReentrantReadWriteLock lock;
    final ReentrantReadWriteLock.ReadLock readLock;
    final ReentrantReadWriteLock.WriteLock writeLock;

    protected CacheLock() {
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

}
