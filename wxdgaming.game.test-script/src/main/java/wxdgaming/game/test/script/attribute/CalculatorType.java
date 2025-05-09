package wxdgaming.game.test.script.attribute;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;

/**
 * 属性计算器类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-09 19:30
 **/
@Getter
public enum CalculatorType {

    BASE(1, "基础属性"),
    EQUIPMENT(2, "装备属性"),
    ;

    private static final Map<Integer, CalculatorType> static_map = MapOf.ofMap(CalculatorType::getCode, CalculatorType.values());

    public static CalculatorType ofOrException(int value) {
        CalculatorType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    CalculatorType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}