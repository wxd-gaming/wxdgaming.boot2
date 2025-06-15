package wxdgaming.game.bean;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.game.bean.buff.BuffType;

import java.util.Map;

/**
 * 常量
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-15 10:11
 **/
@Getter
public enum BuffTypeConst implements BuffType {

    None(0, "默认值"),
    AddHp(1, "增加生命值"),
    AddMp(2, "增加魔法值"),
    ChangeAttr(3, "修改属性值"),

    ;

    private static final Map<Integer, BuffTypeConst> static_map = MapOf.ofMap(BuffTypeConst::getType, BuffTypeConst.values());

    public static BuffTypeConst of(int value) {
        return static_map.get(value);
    }

    public static BuffTypeConst ofOrException(int value) {
        BuffTypeConst tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int type;
    private final String comment;

    BuffTypeConst(int type, String comment) {
        this.type = type;
        this.comment = comment;
    }

}