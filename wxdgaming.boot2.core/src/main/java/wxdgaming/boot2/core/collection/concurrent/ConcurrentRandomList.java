package wxdgaming.boot2.core.collection.concurrent;

import lombok.Getter;
import wxdgaming.boot2.core.collection.ListOf;
import wxdgaming.boot2.core.util.RandomUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * 随机列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 14:40
 **/
@Getter
public class ConcurrentRandomList<T> implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private final StampedLock reentrantLock = new StampedLock();
    private Map<T, Integer> map = new HashMap<>();
    private List<T> list = ListOf.newArrayList();

    public void add(T t) {
        long writeLock = reentrantLock.writeLock();
        try {
            if (!map.containsKey(t)) {
                map.put(t, list.size());
                list.add(t);
            }
        } finally {
            reentrantLock.unlockWrite(writeLock);
        }
    }

    public void remove(T t) {
        long writeLock = reentrantLock.writeLock();
        try {
            Integer index = map.remove(t);
            if (index == null) return;
            if (index < list.size() - 1) {
                list.set(index, list.getLast());
            }
            list.removeLast();
        } finally {
            reentrantLock.unlockWrite(writeLock);
        }
    }

    public T random() {
        long optimisticRead = reentrantLock.tryOptimisticRead();
        T t = null;
        if (!list.isEmpty()) {
            t = list.get(RandomUtils.random(list.size()));
        }
        if (reentrantLock.validate(optimisticRead)) {
            long readLock = reentrantLock.readLock();
            try {
                if (!list.isEmpty()) {
                    t = list.get(RandomUtils.random(list.size()));
                }
            } finally {
                reentrantLock.unlockRead(readLock);
            }
        }
        return t;
    }

}
