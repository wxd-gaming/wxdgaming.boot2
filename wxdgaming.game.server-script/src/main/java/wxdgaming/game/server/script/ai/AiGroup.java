package wxdgaming.game.server.script.ai;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 分组
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 19:41
 **/
@Getter
public enum AiGroup {
    None(0, "默认值"),
    ;

    private static final Map<Integer, AiGroup> static_map = MapOf.ofMap(AiGroup::getCode, AiGroup.values());

    public static AiGroup of(int value) {
        return static_map.get(value);
    }

    public static AiGroup ofOrException(int value) {
        AiGroup tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    AiGroup(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}