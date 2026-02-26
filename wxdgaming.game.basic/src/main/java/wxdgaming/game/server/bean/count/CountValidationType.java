package wxdgaming.game.server.bean.count;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.timer.MyClock;

import java.util.Map;
import java.util.function.Predicate;

/**
 * 条件类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:19
 **/
@Getter
public enum CountValidationType {
    DayCount(4, "每天计数", MyClock::isSameDay),
    WeekCount(5, "每周计数", MyClock::isSameWeek),
    MonthCount(6, "每月计数", MyClock::isSameMonth),
    YearCount(7, "每年计数", MyClock::isSameYear),
    /** 永久 */
    ForeverCount(8, "永久计数", l -> true),
    ;

    private static final Map<Integer, CountValidationType> static_map = MapOf.ofMap(CountValidationType::getCode, CountValidationType.values());

    public static CountValidationType of(int value) {
        return static_map.get(value);
    }

    public static CountValidationType ofOrException(int value) {
        CountValidationType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;
    private final Predicate<Long> check;

    CountValidationType(int code, String comment, Predicate<Long> check) {
        this.code = code;
        this.comment = comment;
        this.check = check;
    }

}