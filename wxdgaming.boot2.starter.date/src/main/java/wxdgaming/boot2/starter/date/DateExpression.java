package wxdgaming.boot2.starter.date;

import wxdgaming.boot2.core.timer.MyClock;

/**
 * 时间表达
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-02-25 19:16
 **/
public record DateExpression(long start, long end) {

    public boolean valid() {
        long now = MyClock.millis();
        return start <= now && now <= end;
    }

    public String fmt() {
        return "DateExpression{start=%s, end=%s}".formatted(MyClock.formatDate(start), MyClock.formatDate(end));
    }
}
