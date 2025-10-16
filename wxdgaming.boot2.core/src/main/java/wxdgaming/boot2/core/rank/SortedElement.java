package wxdgaming.boot2.core.rank;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class SortedElement<K, V extends Comparable<V>> implements Comparable<SortedElement<K, V>> {
    private K k;
    private V v;
    private long updateTime;

    public SortedElement() {
    }

    public SortedElement(K k) {
        this.k = k;
    }

    @Override public int compareTo(SortedElement<K, V> o) {
        int i = o.v.compareTo(this.v);
        if (i == 0) {
            i = Long.compare(this.updateTime, o.updateTime);
        }
        if (i == 0) {
            i = Integer.compare(this.hashCode(), o.hashCode());
        }
        return i;
    }

    @Override public final boolean equals(Object o) {
        if (!(o instanceof SortedElement<?, ?> sortedElement)) return false;

        return Objects.equals(getK(), sortedElement.getK());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getK());
    }

    @Override public String toString() {
        return "Element{k=%s, v=%s, updateTime=%d}".formatted(k, v, updateTime);
    }

}
