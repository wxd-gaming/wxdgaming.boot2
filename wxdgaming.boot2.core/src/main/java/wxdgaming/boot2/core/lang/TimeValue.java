package wxdgaming.boot2.core.lang;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.timer.MyClock;

/**
 * long 类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-18 20:12
 **/
@Getter
@Setter
public class TimeValue {

    private long hold;

    public TimeValue() {
    }

    public TimeValue(long hold) {
        this.hold = hold;
    }

    public TimeValue(String source) {
        String format = "";
        if (source.contains("-"))
            format += "yyyy-MM-dd";
        else
            format += "yyyy/MM/dd";
        int hasLength = StringUtils.hasLength(source, ':');
        if (hasLength == 3)
            format += " HH:mm:ss:SSS";
        else if (hasLength == 2)
            format += " HH:mm:ss";
        else if (hasLength == 1)
            format += " HH:mm";
        else if (StringUtils.hasLength(source, ' ') > 0) {
            format += " HH";
        }
        this.hold = MyClock.parseDate(format, source).getTime();
    }

    public int intMaxValue() {
        if (hold > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) hold;
    }

    public int intValue() {
        return (int) hold;
    }

    public long longValue() {
        return hold;
    }

    public void refresh(long hold) {
        this.hold = hold;
    }

    /** yyyy/MM/dd HH:mm:ss */
    public String dateFormat() {
        return MyClock.formatDate("yyyy/MM/dd HH:mm:ss", hold);
    }

    public String dateFormat(String format) {
        return MyClock.formatDate(format, hold);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TimeValue timeValue = (TimeValue) o;
        return getHold() == timeValue.getHold();
    }

    @Override public int hashCode() {
        return Long.hashCode(getHold());
    }

    @Override public String toString() {
        return dateFormat();
    }
}
