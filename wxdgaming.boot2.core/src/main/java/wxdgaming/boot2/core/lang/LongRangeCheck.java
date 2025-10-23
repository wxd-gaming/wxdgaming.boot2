package wxdgaming.boot2.core.lang;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 区间范围
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 13:43
 **/
@Getter
@Setter
@ToString
public class LongRangeCheck implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private long start;
    private long end;

    public LongRangeCheck() {
    }

    public LongRangeCheck(long start, long end) {
        this.start = start;
        this.end = end;
    }

    /** 默认值0，表示无限制 {@code (start == 0 || start <= check) && (end == 0 || check <= end)} */
    public boolean inRange(long check) {
        return (start == 0 || start <= check) && (end == 0 || check <= end);
    }

    /** 包括默认值限制 {@code start <= check && check <= end} */
    public boolean inRangeNotDefault(long check) {
        return start <= check && check <= end;
    }

}
