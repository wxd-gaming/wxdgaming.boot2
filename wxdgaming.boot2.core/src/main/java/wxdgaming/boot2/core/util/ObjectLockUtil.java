package wxdgaming.boot2.core.util;

import wxdgaming.boot2.core.cache2.CacheLock;
import wxdgaming.boot2.core.cache2.LRUCache;

import java.util.concurrent.TimeUnit;
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
    static final LRUCache<Object, LockObject> cache;
    static final CacheLock cacheLock;
    static final ReentrantReadWriteLock.WriteLock writeLock;

    static {
        cache = LRUCache.<Object, LockObject>builder()
                .area(1)
                .heartTimeMs(TimeUnit.MINUTES.toMillis(5))
                .expireAfterReadMs(TimeUnit.MINUTES.toMillis(5))
                .loader(k -> new LockObject())
                .removalListener((o, lockObject) -> lockObject.count <= 0)
                .build();
        cache.start();
        cacheLock = cache.getReentrantLocks().getFirst();
        writeLock = cacheLock.getWriteLock();
    }

    private static LockObject getLockObject(Object key) {
        LockObject lockObject;
        writeLock.lock();
        try {
            lockObject = cache.get(key);
            lockObject.count++;
        } finally {
            writeLock.unlock();
        }
        return lockObject;
    }

    public static void lock(Object key) {
        getLockObject(key).reentrantLock.lock();
    }

    public static boolean tryLock(Object key) {
        return getLockObject(key).reentrantLock.tryLock();
    }

    public static boolean tryLock(Object key, long timeout, TimeUnit unit) throws InterruptedException {
        return getLockObject(key).reentrantLock.tryLock(timeout, unit);
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
        final ReentrantLock reentrantLock = new ReentrantLock();

    }

}
