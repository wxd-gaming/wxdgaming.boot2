package wxdgaming.boot2.core.util;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁,单例锁
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-15 20:54
 **/
public class ObjectLockUtil {

    static final ReentrantLock GlobalLock = new ReentrantLock();
    private static final HashMap<Object, LockObject> _instances = new HashMap<>();

    public static void lock(Object key) {
        LockObject lockObject;
        GlobalLock.lock();
        try {
            lockObject = _instances.computeIfAbsent(key, k -> new LockObject());
            lockObject.count++;
        } finally {
            GlobalLock.unlock();
        }
        lockObject.reentrantLock.lock();
    }

    public static void unlock(Object key) {
        LockObject lockObject;
        GlobalLock.lock();
        try {
            lockObject = _instances.get(key);
            if (lockObject != null) {
                lockObject.count--;
                if (lockObject.count <= 0) {
                    _instances.remove(key);
                }
            }
        } finally {
            GlobalLock.unlock();
        }
        lockObject.reentrantLock.unlock();
    }

    private static class LockObject {

        int count = 0;
        ReentrantLock reentrantLock = new ReentrantLock();

    }

}
