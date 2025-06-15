package wxdgaming.game.bean.buff;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.util.AssertUtil;

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
    ChangeHpMp(1, "增加生命值"),
    ChangeAttr(2, "修改属性值"),

    ;

    private static final Map<Integer, BuffTypeConst> static_map = MapOf.ofMap(BuffTypeConst::getType, BuffTypeConst.values());

    public static BuffTypeConst of(int value) {
        return static_map.get(value);
    }

    public static BuffTypeConst ofOrException(int value) {
        BuffTypeConst tmp = static_map.get(value);
        AssertUtil.assertNull(tmp, "查找失败 " + value);
        return tmp;
    }

    private final int type;
    private final String comment;

    BuffTypeConst(int type, String comment) {
        this.type = type;
        this.comment = comment;
    }

}