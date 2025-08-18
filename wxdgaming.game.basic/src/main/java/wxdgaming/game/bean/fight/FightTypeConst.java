package wxdgaming.game.bean.fight;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 战斗类型常量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-15 10:07
 **/
@Getter
public enum FightTypeConst implements FightType {

    None(0, "默认值"),

    ;

    private static final Map<Integer, FightTypeConst> static_map = MapOf.ofMap(FightTypeConst::getType, FightTypeConst.values());

    public static FightTypeConst of(int value) {
        return static_map.get(value);
    }


    public static FightTypeConst ofOrException(int value) {
        FightTypeConst tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int type;
    private final String comment;

    FightTypeConst(int code, String comment) {
        this.type = code;
        this.comment = comment;
    }

}