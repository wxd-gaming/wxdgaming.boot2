package wxdgaming.boot2.core.lang;

import lombok.Getter;

/**
 * 时间差记录器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-15 14:19
 **/
@Getter
public class DiffTime {

    protected long startTime;

    public DiffTime() {
        this(System.nanoTime());
    }

    public DiffTime(long startTime) {
        this.startTime = startTime;
    }

    public void reset() {
        this.startTime = System.nanoTime();
    }

    /** 从初始化到调用的时间差 */
    public float diff() {
        return (System.nanoTime() - startTime) / 100 / 10000f;
    }

    /** 从初始化到调用的时间差 */
    public long diff100() {
        return (System.nanoTime() - startTime) / 10000;
    }

    /** 从初始化到调用的时间差 */
    public long diffLong() {
        return (System.nanoTime() - startTime) / 1000000;
    }

}
