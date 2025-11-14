package wxdgaming.boot2.core.util;

import wxdgaming.boot2.core.cache.LRUCacheLock;
import wxdgaming.boot2.core.locks.MonitorReadWrite;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 重入锁,单例锁
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-12-15 20:54
 **/
public class SingletonLockUtil {

    /** 缓存锁 */
    static final LRUCacheLock<Object, LockObject> cache;
    static final MonitorReadWrite MONITOR_READ_WRITE;

    static {
        cache = LRUCacheLock.<Object, LockObject>builder()
                .blockSize(1)
                .heartExpireAfterWrite(Duration.ofMinutes(1))
                .expireAfterAccess(Duration.ofMinutes(5))
                .loader(k -> new LockObject())
                .removalListener((o, lockObject, removalCause) -> lockObject.count <= 0)
                .build();
        MONITOR_READ_WRITE = cache.getMonitorReadWrite();
    }

    private static LockObject getLockObject(Object key) {
        LockObject lockObject;
        MONITOR_READ_WRITE.writeLock();
        try {
            lockObject = cache.get(key);
            lockObject.count++;
        } finally {
            MONITOR_READ_WRITE.unWriteLock();
        }
        return lockObject;
    }

    public static void lock(Object key) {
        getLockObject(key).reentrantLock.lock();
    }

    public static void lockRunning(Object key, Runnable runnable) {
        lock(key);
        try {
            runnable.run();
        } finally {
            unlock(key);
        }
    }

    public static <R> R lockRunning(Object key, Supplier<R> runnable) {
        lock(key);
        try {
            return runnable.get();
        } finally {
            unlock(key);
        }
    }

    public static boolean tryLock(Object key) {
        return getLockObject(key).reentrantLock.tryLock();
    }

    public static boolean tryLock(Object key, long timeout, TimeUnit unit) throws InterruptedException {
        return getLockObject(key).reentrantLock.tryLock(timeout, unit);
    }

    public static void unlock(Object key) {
        LockObject lockObject;
        MONITOR_READ_WRITE.writeLock();
        try {
            lockObject = cache.get(key);
            lockObject.count--;
        } finally {
            MONITOR_READ_WRITE.unWriteLock();
        }
        lockObject.reentrantLock.unlock();
    }

    private static class LockObject {

        int count = 0;
        final ReentrantLock reentrantLock = new ReentrantLock();

    }

}
