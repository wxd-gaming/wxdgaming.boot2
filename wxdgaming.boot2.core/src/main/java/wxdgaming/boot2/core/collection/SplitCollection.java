package wxdgaming.boot2.core.collection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.format.data.Data2Json;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 块集合
 * <p> 非线程安全的
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-10-25 12:05
 **/
@Slf4j
@Getter
public class SplitCollection<E> extends ObjectBase implements Serializable, Data2Json {

    private final ReentrantLock lock = new ReentrantLock();
    /** 切割块大小 */
    private final int splitOrg;
    private final boolean linked;
    private volatile int size = 0;
    private final LinkedList<List<E>> es = new LinkedList<>();

    public SplitCollection() {
        this(1000);
    }

    /** 块大小 */
    public SplitCollection(int splitOrg) {
        this(splitOrg, false);
    }

    /** 块大小 */
    public SplitCollection(int splitOrg, Collection<E> es) {
        this(splitOrg, false);
        addAll(es);
    }


    public SplitCollection(int splitOrg, boolean linked) {
        this.splitOrg = splitOrg;
        this.linked = linked;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size <= 0;
    }

    private void init() {
        if (linked) {
            es.add(new LinkedList<>());
        } else {
            es.add(new ArrayList<>(splitOrg + 1));
        }
    }

    public boolean add(E e) {
        if (es.isEmpty()) {
            init();
        }
        List<E> last = es.getLast();
        last.add(e);
        size++;
        if (last.size() >= splitOrg) {
            init();
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }
        return true;
    }

    public List<E> first() {
        return es.getFirst();
    }

    public List<E> removeFirst() {
        final List<E> es = this.es.removeFirst();
        size -= es.size();
        return es;
    }

    public void clear() {
        es.clear();
        size = 0;
    }

}
