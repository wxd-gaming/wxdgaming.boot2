package wxdgaming.boot2.core.timer;

import lombok.Getter;

/**
 * 表达式有效期
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-24 13:15
 **/
@Getter
public class CronDuration {

    private final String cron;
    private final long start;
    private final long end;

    public CronDuration(String cron, long start, long end) {
        this.cron = cron;
        this.start = start;
        this.end = end;
    }

    public String formatStartTime() {
        return MyClock.formatDate(start);
    }

    public String formatEndTime() {
        return MyClock.formatDate(end);
    }

    @Override public String toString() {
        return "CronDuration{cron=%s, start=%s, end=%s}".formatted(cron, formatStartTime(), formatEndTime());
    }
}
