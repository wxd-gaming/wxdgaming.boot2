package wxdgaming.boot2.core.util;

import wxdgaming.boot2.core.cache2.CacheLock;
import wxdgaming.boot2.core.cache2.LRUCache;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 重入锁,单例锁
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-15 20:54
 **/
public class ObjectLockUtil {

    /** 缓存锁 */
    static final LRUCache<Object, LockObject> cache = LRUCache.<Object, LockObject>builder()
            .area(1)
            .heartTimeMs(3000)
            .expireAfterReadMs(3000)
            .loader(k -> new LockObject())
            .removalListener((o, lockObject) -> lockObject.count <= 0)
            .build();

    static final CacheLock cacheLock = cache.getReentrantLocks().getFirst();
    static final ReentrantReadWriteLock.WriteLock writeLock = cacheLock.getWriteLock();

    public static void lock(Object key) {
        LockObject lockObject;
        writeLock.lock();
        try {
            lockObject = cache.get(key);
            lockObject.count++;
        } finally {
            writeLock.unlock();
        }
        lockObject.reentrantLock.lock();
    }

    public static void unlock(Object key) {
        LockObject lockObject;
        writeLock.lock();
        try {
            lockObject = cache.get(key);
            lockObject.count--;
        } finally {
            writeLock.unlock();
        }
        lockObject.reentrantLock.unlock();
    }

    private static class LockObject {

        int count = 0;
        ReentrantLock reentrantLock = new ReentrantLock();

    }

}
