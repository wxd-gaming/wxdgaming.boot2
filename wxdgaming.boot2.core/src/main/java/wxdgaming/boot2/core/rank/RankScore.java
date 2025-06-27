package wxdgaming.boot2.core.rank;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.HashMap;
import java.util.Objects;

/**
 * 排行榜分数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 20:57
 **/
@Getter
@Setter
@Accessors(chain = true)
public class RankScore extends ObjectBase implements Comparable<RankScore> {

    private String key;
    /**
     * 严谨性，如果副本3星通关最短时间者排名最靠前，
     * <p>假设副本存在最大时间是9999，用时最短实际分数越大
     * <p>1,应该 考虑 （9999-时间）*10+星数 算法
     * <p>2,也可以使用 通关时间 * 系数 +星星数 * 系数
     */
    private long score;
    private long timestamp;
    private HashMap<String, String> other = new HashMap<>();

    @Override public int compareTo(RankScore o) {
        if (this.score != o.score)
            return Long.compare(o.score, this.score);
        if (this.timestamp != o.timestamp)
            return Long.compare(this.timestamp, o.timestamp);
        return this.key.compareTo(o.key);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RankScore rankScore = (RankScore) o;
        return Objects.equals(getKey(), rankScore.getKey());
    }

    @Override public int hashCode() {
        return Objects.hashCode(getKey());
    }

}
