package wxdgaming.game.server.script.validation;

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
public enum ConditionType {
    None(0, "默认值"),
    Level(1, "等级"),
    ;

    private static final Map<Integer, ConditionType> static_map = MapOf.ofMap(ConditionType::getCode, ConditionType.values());

    public static ConditionType of(int value) {
        return static_map.get(value);
    }

    public static ConditionType ofOrException(int value) {
        ConditionType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    ConditionType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}