package wxdgaming.boot2.core.format;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.timer.MyClock;

import java.io.Serializable;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-07-02 14:36
 **/
@Slf4j
public class NewId implements Serializable {
    /** 相当于1970年1月1日，到2024年9月24日 经过了这么多天 */
    public static final int OffSetDays = 19990;

    /** 8位 */
    public static final long Offset8 = 0xFF;
    /** 20位 */
    public static final long Offset20 = 0xFFFFF;
    public static final long Offset32 = 0xFFFFFFFFL;
    public static final long Offset35 = 0x7FFFFFFFFL;

    final long hexId;
    final long type;

    volatile long lastDays = 0;
    volatile long lastSecondByDay = 0;
    volatile long seed = 0;

    public NewId(int hexId, int type) {

        if (hexId > Offset20) throw new RuntimeException("hexId 不能大于 " + Offset20);
        if (type > Offset8) throw new RuntimeException("type 不能大于 " + Offset8);

        this.hexId = hexId;
        this.type = type;

    }

    public synchronized long newId() {
        long nanoTime = System.currentTimeMillis();
        final long days = MyClock.days(nanoTime) - OffSetDays;
        final long secondByDay = MyClock.dayOfSecond(nanoTime);
        if (lastDays != days || secondByDay != lastSecondByDay) {
            seed = 0;
            lastDays = days;
            lastSecondByDay = secondByDay;
        }
        long id = (days << 17 | secondByDay) << 18;
        seed++;
        id += seed;
        //        System.out.println("idValue=" + id);
        return type << 55 | hexId << 35 | id;
    }

    public int hexId(long value) {
        return (int) (value >> 35 & Offset20);
    }

    public int type(long value) {
        return (int) (value >> 55 & Offset8);
    }

    public long idValue(long value) {
        return (value & Offset35);
    }

}
