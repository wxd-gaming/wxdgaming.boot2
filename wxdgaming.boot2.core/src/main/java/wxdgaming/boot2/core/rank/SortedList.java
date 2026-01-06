package wxdgaming.boot2.core.rank;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.locks.MonitorReadWrite;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * 自动排序
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-16 13:27
 **/
@Getter
@Setter
public class SortedList<K, V extends Comparable<V>> extends MonitorReadWrite {

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
        syncWrite(() -> {
            SortedElement<K, V> sortedElement = map.computeIfAbsent(k, SortedElement::new);
            V oldV = sortedElement.getV();
            if (!Objects.equals(v, oldV)) {
                if (oldV != null) {
                    sortSet.remove(sortedElement);
                }
                sortedElement.setV(v);
                sortedElement.setUpdateTime(updateTime);
                sortSet.add(sortedElement);
            }
        });
    }

    /** 如果是保存数据库，请调用这个函数 */
    public List<SortedElement<K, V>> toList() {
        return supplierRead(() -> map.values().stream().toList());
    }

    /** 这里是拷贝数据，避免获取数据之后，数据被修改 */
    public List<Element> toSortElement() {
        return supplierRead(() -> sortSet.stream().map(e -> new Element(e.getK(), e.getV())).toList());
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
