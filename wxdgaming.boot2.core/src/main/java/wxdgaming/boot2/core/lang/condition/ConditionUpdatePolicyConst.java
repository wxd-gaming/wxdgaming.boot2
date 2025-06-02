package wxdgaming.boot2.core.lang.condition;

import lombok.Getter;

/**
 * 更新操作
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-01 13:50
 **/
@Getter
public enum ConditionUpdatePolicyConst implements ConditionUpdatePolicy {

    /** 累加 */
    Add("+") {
        @Override public long update(long value, long target) {
            return Math.addExact(value, target);
        }
    },
    /** 直接替换 */
    Replace("=") {
        @Override public long update(long value, long target) {
            return target;
        }
    },
    /** 取最大值 */
    Max("max") {
        @Override public long update(long value, long target) {
            return Math.max(value, target);
        }
    },
    /** 取最小值 */
    Min("min") {
        @Override public long update(long value, long target) {
            return Math.min(value, target);
        }
    },
    ;

    private final String comment;

    private ConditionUpdatePolicyConst(String comment) {
        this.comment = comment;
    }

}
