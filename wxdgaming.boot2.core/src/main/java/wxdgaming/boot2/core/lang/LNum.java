package wxdgaming.boot2.core.lang;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * long 类型 数量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-08-09 14:30
 **/
@Getter
@Setter
@Accessors(chain = true)
public class LNum extends ObjectBaseRWLock implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    protected volatile long num = 0;

    public LNum() {
    }

    public LNum(long num) {
        this.num = num;
    }

    public void clear() {
        syncWrite(() -> this.num = 0);
    }

    public int intValue() {
        if (this.num >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) this.num;
    }

    public LNum setNum(long change) {
        syncWrite(() -> this.num = change);
        return this;
    }

    /** 加法 */
    public long add(long val) {
        return add(val, null);
    }

    /** 加法 */
    public long add(long val, Long max) {
        return add(val, null, max);
    }

    /** 加法 */
    public long add(long val, Long min, Long max) {
        syncWrite(() -> {
            long change = Math.addExact(this.num, val);
            if (min != null) {
                /*有最小值，实际上就是谁最大取谁*/
                change = Math.max(min, change);
            }
            if (max != null) {
                /*有最大值，实际上就是谁最小取谁*/
                change = Math.min(max, change);
            }
            this.num = change;
        });
        return getNum();
    }

    /** 减法 */
    public long sub(long val) {
        return sub(val, null);
    }

    /** 减法 */
    public long sub(long val, Long min) {
        return sub(val, min, null);

    }

    /** 减法 */
    public long sub(long val, Long min, Long max) {
        syncWrite(() -> {
            long change = Math.subtractExact(this.num, val);
            if (min != null) {
                /*有最小值，实际上就是谁最大取谁*/
                change = Math.max(min, change);
            }
            if (max != null) {
                /*有最大值，实际上就是谁最小取谁*/
                change = Math.min(max, change);
            }
            this.num = change;
        });
        return getNum();
    }


    /** 如果更新成功返回 true */
    public boolean min(long val) {
        return supplierWrite(() -> {
            long oldVal = this.num;
            setNum(Math.min(this.num, val));
            return getNum() != oldVal;
        });
    }

    /** 如果更新成功返回 true */
    public boolean max(long val) {
        writeLock();
        try {
            long oldVal = this.num;
            setNum(Math.max(this.num, val));
            return getNum() != oldVal;
        } finally {
            unWriteLock();
        }
    }

    @Override public String toString() {
        return String.valueOf(num);
    }

}
