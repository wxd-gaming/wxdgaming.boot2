package wxdgaming.game.bean.attr;


import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 属性类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-03-23 18:23
 **/
@Getter
public enum AttrType {
    HP(1, "生命"),
    MAXHP(2, "最大生命"),
    体力(3, "体力"),
    攻击(4, "攻击力"),
    防御(5, "防御"),
    MP(101, "魔法"),
    MAXMP(102, "最大魔法"),
    ;

    private static final Map<Integer, AttrType> static_map = MapOf.ofMap(AttrType::getCode, AttrType.values());

    public static AttrType as(int value) {
        return static_map.get(value);
    }

    private final int code;
    private final String comment;

    AttrType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}