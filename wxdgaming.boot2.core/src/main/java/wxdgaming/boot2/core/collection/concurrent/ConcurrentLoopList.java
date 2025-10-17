package wxdgaming.boot2.core.collection.concurrent;

import wxdgaming.boot2.core.lang.ObjectBaseRWLock;
import wxdgaming.boot2.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 循环列表
 *
 * @param <T>
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-09-07 12:05
 */
public class ConcurrentLoopList<T> extends ObjectBaseRWLock {

    private final AtomicLong atomicLong = new AtomicLong();
    private final ArrayList<T> list = new ArrayList<>();

    public boolean add(T t) {
        return supplierWrite(() -> list.add(t));
    }

    public boolean remove(T t) {
        return supplierWrite(() -> list.remove(t));
    }

    public void forEach(Consumer<T> consumer) {
        syncRead(() -> list.forEach(consumer));
    }

    /** 拷贝一个副本 */
    public List<T> duplicate() {
        return supplierWrite(() -> new ArrayList<>(list));
    }

    /** 循环获取 如果 null 则会引发异常 */
    public T loopNullException() {
        T loop = loop();
        AssertUtil.nullEmpty(loop, "当前链接 empty ");
        return loop;
    }

    /** 循环获取 */
    public T loop() {
        return supplierRead(() -> {
            if (list.isEmpty()) return null;
            long andIncrement = atomicLong.getAndIncrement();
            if (andIncrement > Integer.MAX_VALUE) {
                atomicLong.set(0);
            }
            int index = (int) (andIncrement % list.size());
            return list.get(index);
        });
    }

}
