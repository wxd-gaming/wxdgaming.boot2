package wxdgaming.game.server.bean.ai;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;
import java.util.function.Function;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-10-23 19:41
 **/
@Getter
public enum AiAction {
    Idle(1, 1, "休息", AiActionData::new),
    Move(2, 1, "移动", MoveAiActionData::new),
    FindPath(3, 1, "寻路", MoveAiActionData::new),
    UseSkill(4, 3, "使用技能", AiActionData::new),
    ;

    private static final Map<Integer, AiAction> static_map = MapOf.ofMap(AiAction::getCode, AiAction.values());

    public static AiAction of(int value) {
        return static_map.get(value);
    }

    public static AiAction ofOrException(int value) {
        AiAction tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final int group;
    private final String comment;
    private final Function<AiAction, AiActionData> newData;

    AiAction(int code, int group, String comment, Function<AiAction, AiActionData> newData) {
        this.code = code;
        this.group = group;
        this.comment = comment;
        this.newData = newData;
    }

}