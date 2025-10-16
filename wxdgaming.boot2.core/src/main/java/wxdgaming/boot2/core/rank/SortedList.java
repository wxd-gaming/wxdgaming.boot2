package wxdgaming.boot2.core.rank;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自动排序
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 13:27
 **/
@Getter
@Setter
public class SortedList<K, V extends Comparable<V>> {

    @JSONField(serialize = false, deserialize = false)
    private final transient ReentrantLock reentrantLock = new ReentrantLock();
    private final HashMap<K, SortedElement<K, V>> map = new HashMap<>();
    @JSONField(serialize = false, deserialize = false)
    private final transient TreeSet<SortedElement<K, V>> sortSet = new TreeSet<>();

    public SortedList() {
    }

    public SortedList(List<SortedElement<K, V>> list) {
        for (SortedElement<K, V> element : list) {
            map.put(element.getK(), element);
            sortSet.add(element);
        }
    }

    public void update(K k, V v) {
        update(k, v, System.nanoTime());
    }

    public void update(K k, V v, long updateTime) {
        reentrantLock.lock();
        try {
            SortedElement<K, V> sortedElement = map.computeIfAbsent(k, l -> new SortedElement<K, V>(l));
            V oldV = sortedElement.getV();
            if (!Objects.equals(v, oldV)) {
                if (oldV != null) {
                    sortSet.remove(sortedElement);
                }
                sortedElement.setV(v);
                sortedElement.setUpdateTime(updateTime);
                sortSet.add(sortedElement);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public List<SortedElement<K, V>> toList() {
        reentrantLock.lock();
        try {
            return map.values().stream().toList();
        } finally {
            reentrantLock.unlock();
        }
    }

    public List<Element> toSortElement() {
        reentrantLock.lock();
        try {
            return sortSet.stream().map(e -> new Element(e.getK(), e.getV())).toList();
        } finally {
            reentrantLock.unlock();
        }
    }

    @Getter
    public class Element {
        private final K k;
        private final V v;

        public Element(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override public String toString() {
            return "Element{k=%s, v=%s}".formatted(k, v);
        }
    }
}
