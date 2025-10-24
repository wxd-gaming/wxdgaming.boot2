package wxdgaming.game.server.script.ai;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 19:41
 **/
@Getter
public enum AiType {
    None(0, AiGroup.None, "默认值"),
    ;

    private static final Map<Integer, AiType> static_map = MapOf.ofMap(AiType::getCode, AiType.values());

    public static AiType of(int value) {
        return static_map.get(value);
    }

    public static AiType ofOrException(int value) {
        AiType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final AiGroup group;
    private final String comment;

    AiType(int code, AiGroup group, String comment) {
        this.code = code;
        this.group = group;
        this.comment = comment;
    }

}