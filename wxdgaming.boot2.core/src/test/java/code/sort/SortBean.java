package code.sort;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-26 09:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public class SortBean implements Comparable<SortBean> {

    private String key;
    private long overTime;

    @Override public int compareTo(SortBean o) {
        if (this.overTime != o.overTime) {
            return (int) (this.overTime - o.overTime);
        }
        return Integer.compare(this.hashCode(), o.hashCode());
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SortBean sortBean = (SortBean) o;
        return Objects.equals(getKey(), sortBean.getKey());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getKey());
    }

    @Override public String toString() {
        return String.valueOf(overTime);
    }
}
