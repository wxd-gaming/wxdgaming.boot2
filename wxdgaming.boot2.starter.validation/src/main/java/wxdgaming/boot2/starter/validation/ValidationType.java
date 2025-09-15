package wxdgaming.boot2.starter.validation;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 条件类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 11:19
 **/
@Getter
public enum ValidationType {
    None(0, "默认值"),
    Level(1, "等级"),
    Job(2, "职业"),
    OpenDay(3, "开服天数"),
    ;

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