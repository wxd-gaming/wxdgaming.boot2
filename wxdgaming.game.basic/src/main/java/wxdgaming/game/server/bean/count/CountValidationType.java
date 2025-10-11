package wxdgaming.game.server.bean.count;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.validation.IValidationType;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationEquals;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 条件类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:19
 **/
@Getter
public enum CountValidationType implements IValidationType {
    DayCount(4, "每天计数", MyClock::isSameDay),
    WeekCount(5, "每周计数", MyClock::isSameWeek),
    MonthCount(6, "每月计数", MyClock::isSameMonth),
    ;

    public static final Function<String, List<Validation>> Parse = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(CountValidationType.valueOf(vs[0]), ValidationEquals.valueOf(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

    public static final Function<String, List<Validation>> Parse2 = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(CountValidationType.valueOf(vs[0]), ValidationEquals.of2OrException(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

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