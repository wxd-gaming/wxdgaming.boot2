package wxdgaming.game.server.script.validation;

import lombok.Getter;
import wxdgaming.boot2.core.collection.MapOf;

import java.util.Map;
import java.util.function.BiPredicate;

/**
 * 比较器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-11 14:08
 **/
@Getter
public enum ValidationEquals {
    /** == */
    eq(1, "==", "等于", Long::equals),
    /** != */
    ne(2, "!=", "不等于", (a, b) -> !a.equals(b)),
    /** > */
    gt(3, ">", "大于", (a, b) -> a > b),
    /** >= */
    gte(4, ">=", "大于等于", (a, b) -> a >= b),
    /** < */
    lt(5, "<", "小于", (a, b) -> a < b),
    /** <= */
    lte(6, "<=", "小于等于", (a, b) -> a <= b),
    ;

    private static final Map<Integer, ValidationEquals> static_map = MapOf.ofMap(ValidationEquals::getCode, ValidationEquals.values());
    private static final Map<String, ValidationEquals> static2_map = MapOf.ofMap(ValidationEquals::getCodeString, ValidationEquals.values());

    public static ValidationEquals of(int value) {
        return static_map.get(value);
    }

    public static ValidationEquals ofOrException(int value) {
        ValidationEquals tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    public static ValidationEquals of2OrException(String value) {
        ValidationEquals tmp = static2_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String codeString;
    private final String comment;
    private final BiPredicate<Long, Long> predicate;

    ValidationEquals(int code, String codeString, String comment, BiPredicate<Long, Long> predicate) {
        this.code = code;
        this.codeString = codeString;
        this.comment = comment;
        this.predicate = predicate;
    }

}