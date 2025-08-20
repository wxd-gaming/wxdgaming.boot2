package code.queue;


import wxdgaming.boot2.core.util.JvmUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * 循环队列，线程安全的队列
 * <p> 假设1秒钟5000万次add，队列的可用时长是long.maxvalue() /5000w / 60 /60 /24 / 365=5000年
 *
 * @param <T>
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-09 11:21
 */
public class RingQueue<T> {

    static final AtomicBoolean stop = new AtomicBoolean(false);

    static {
        JvmUtil.addShutdownHook(() -> {
            stop.set(true);
        });
    }

    Object[] array;
    final AtomicLong takeIndex = new AtomicLong();
    final AtomicLong addIndex = new AtomicLong();
    final AtomicBoolean close = new AtomicBoolean();

    public RingQueue(int capacity) {
        this.array = new Object[capacity];
    }

    public int size() {
        return (int) (addIndex.get() - takeIndex.get());
    }

    public boolean add(T t) {
        /*类似的cas的无锁效果实现并发*/
        long addIncrement = addIndex.incrementAndGet();
        while (addIncrement - takeIndex.get() > array.length) {
            /*锁1纳秒*/
            LockSupport.parkNanos(1L);
            if (isClose()) {
                return false;
            }
        }
        addIncrement--;
        int minSequence = (int) (addIncrement % array.length);
        array[minSequence] = t;
        return true;
    }

    public T take() {
        /*类似的cas的无锁效果实现并发*/
        long takeIncrement = takeIndex.incrementAndGet();
        while (addIndex.get() - takeIncrement < 0) {
            /*锁1纳秒*/
            LockSupport.parkNanos(1L);
            if (isClose()) {
                return null;
            }
        }
        takeIncrement--;
        int minSequence = (int) (takeIncrement % array.length);
        T t = (T) array[minSequence];
        array[minSequence] = null;
        return t;
    }

    public void close() {
        close.set(true);
    }

    public boolean isClose() {
        return close.get() || stop.get();
    }

}
