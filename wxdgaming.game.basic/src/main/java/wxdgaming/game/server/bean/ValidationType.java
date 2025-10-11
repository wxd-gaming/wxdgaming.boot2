package wxdgaming.game.server.bean;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.starter.validation.IValidationType;
import wxdgaming.boot2.starter.validation.Validation;
import wxdgaming.boot2.starter.validation.ValidationEquals;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 条件类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:19
 **/
@Getter
public enum ValidationType implements IValidationType {
    None(0, "默认值"),
    Level(1, "等级"),
    Job(2, "职业"),
    OpenDay(3, "开服天数"),
    ;

    public static final Function<String, List<Validation>> Parse = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(ValidationType.valueOf(vs[0]), ValidationEquals.valueOf(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

    public static final Function<String, List<Validation>> Parse2 = (string) -> {
        String[] split = string.split(";");
        List<Validation> list = new java.util.ArrayList<>();
        for (String s : split) {
            String[] vs = s.split("[|]");
            Validation validation = new Validation(ValidationType.valueOf(vs[0]), ValidationEquals.of2OrException(vs[1]), Long.parseLong(vs[2]));
            list.add(validation);
        }
        return list;
    };

    private static final Map<Integer, ValidationType> static_map = MapOf.ofMap(ValidationType::getCode, ValidationType.values());

    public static ValidationType of(int value) {
        return static_map.get(value);
    }

    public static ValidationType ofOrException(int value) {
        ValidationType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    ValidationType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}