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

    /** 从初始化到调用的时间差, 单位纳秒 */
    public long diffNs() {
        return (System.nanoTime() - startTime);
    }

    /** 从初始化到调用的时间差, 单位微妙 */
    public long diffUs() {
        return diffNs() / 1000;
    }

    /** 从初始化到调用的时间差，单位微妙, 保留3位小数 */
    public float diffUs5() {
        return diffNs() / 1000f;
    }

    /** 从初始化到调用的时间差，单位毫秒 */
    public long diffMs() {
        return diffNs() / 1000_000;
    }

    /** 从初始化到调用的时间差，单位毫秒 */
    public long diffMsAndReset() {
        long l = diffMs();
        reset();
        return l;
    }

    /** 从初始化到调用的时间差，单位毫秒, 保留5位小数 */
    public float diffMs5() {
        return diffNs() / 10 / 100000f;
    }

    /** 从初始化到调用的时间差，单位毫秒, 保留5位小数 */
    public float diffMs5AndReset() {
        float l = diffMs5();
        reset();
        return l;
    }
}
