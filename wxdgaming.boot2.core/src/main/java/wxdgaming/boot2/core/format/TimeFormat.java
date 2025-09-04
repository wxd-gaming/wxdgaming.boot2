package wxdgaming.boot2.core.format;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.util.ConvertUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 时间格式化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2020-11-21 17:39
 **/
@Getter
@Setter
@Accessors(chain = true)
public class TimeFormat implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    public static String of(long costTime) {
        return new TimeFormat().addTime(costTime).toString();
    }

    @Getter
    public static enum FormatInfo {
        All(0, ""),
        Day(24f * 60 * 60 * 1000, "d"),
        Hour(60f * 60 * 1000, "h"),
        Minute(60 * 1000f, "m"),
        Second(1000f, "s"),
        MS(1f, "ms");
        private final float code;
        private final String comment;

        FormatInfo(float code, String comment) {
            this.code = code;
            this.comment = comment;
        }

    }

    /** 为了保留两位小数, 精度是 毫秒 * 100 */
    private long allTime;

    /**
     * 为了保留两位小数
     *
     * @param cost 精度是 毫秒 * 100
     * @return
     */
    public TimeFormat addTime(long cost) {
        allTime += cost;
        return this;
    }

    private void allInfo(StringBuilder stringBuilder) {
        long ms = allTime / 100 % 1000;

        long s = allTime / 100 / 1000 % 60;
        long m = allTime / 100 / 1000 / 60;

        long h = m / 60;

        m = m % 60;

        long d = h / 24;
        h = h % 24;


        if (d > 0) {
            String format = "%02d %02d:%02d:%02d.%03d";
            stringBuilder.append(String.format(format, d, h, m, s, ms));
        } else if (h > 0) {
            String format = "%02d:%02d:%02d.%03d";
            stringBuilder.append(String.format(format, h, m, s, ms));
        } else {
            String format = "%02d:%02d.%03d";
            stringBuilder.append(String.format(format, m, s, ms));
        }
    }

    private void formatInfo(FormatInfo formatInfo, StringBuilder stringBuilder) {
        Object obj = allTime;
        if (formatInfo != FormatInfo.MS) {
            obj = ConvertUtil.float2(allTime / 100 / formatInfo.getCode());
        }
        stringBuilder.append(StringUtils.leftPad(String.valueOf(obj), 12, ' ')).append(" ").append(formatInfo.getComment());
    }

    public void toString(FormatInfo formatInfo, StringBuilder stringBuilder) {
        if (formatInfo == FormatInfo.All) {
            allInfo(stringBuilder);
        } else {
            formatInfo(formatInfo, stringBuilder);
        }
    }

    public String toString(FormatInfo formatInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        if (formatInfo == FormatInfo.All) {
            allInfo(stringBuilder);
        } else {
            formatInfo(formatInfo, stringBuilder);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return toString(FormatInfo.All);
    }

}
