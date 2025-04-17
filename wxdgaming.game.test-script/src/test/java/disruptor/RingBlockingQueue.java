package disruptor;


import wxdgaming.boot2.core.util.JvmUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 循环队列，线程安全的队列
 * <p> 假设1秒钟5000万次add，队列的可用时长是5000年
 *
 * @param <T>
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-09 11:21
 */
public class RingBlockingQueue<T> {

    static final AtomicBoolean shutdown = new AtomicBoolean(false);

    static {
        JvmUtil.addShutdownHook(() -> {
            shutdown.set(true);
        });
    }

    ReentrantLock reentrantLock = new ReentrantLock();
    Condition condition = reentrantLock.newCondition();

    Object[] array;
    long takeIndex = 0;
    long addIndex = 0;

    public RingBlockingQueue(int capacity) {
        this.array = new Object[capacity];
    }

    public int size() {
        return (int) (addIndex - takeIndex);
    }

    public void add(T t) throws InterruptedException {
        reentrantLock.lock();
        try {
            if (size() >= array.length) {
                condition.await();
            }
            /*类似的cas的无锁效果实现并发*/
            long addIncrement = addIndex++;
            int minSequence = (int) (addIncrement % array.length);
            array[minSequence] = t;
        } finally {
            condition.signal();
            reentrantLock.unlock();
        }
    }

    public T take() throws InterruptedException {
        reentrantLock.lock();
        try {
            if (size() < 1) {
                condition.await();
            }
            /*类似的cas的无锁效果实现并发*/
            long takeIncrement = takeIndex++;
            int minSequence = (int) (takeIncrement % array.length);
            T t = (T) array[minSequence];
            array[minSequence] = null;
            return t;
        } finally {
            condition.signal();
            reentrantLock.unlock();
        }
    }

    public void close() {
    }

    public boolean isClose() {
        return false;
    }

}
