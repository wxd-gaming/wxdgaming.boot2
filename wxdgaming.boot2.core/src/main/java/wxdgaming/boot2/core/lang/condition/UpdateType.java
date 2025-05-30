package wxdgaming.boot2.core.lang.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.AssertUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 进度值的变更方式
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-10 15:46
 **/
@Getter
@Slf4j
public abstract class UpdateType implements Serializable {

    private static final Map<Integer, UpdateType> static_map = new HashMap<>();

    public static UpdateType of(int value) {
        UpdateType updateType = static_map.get(value);
        AssertUtil.assertTrue(updateType != null, "进度变更方式不存在 value=%d", value);
        return updateType;
    }


    /** 累加 */
    public static final UpdateType Add = new UpdateType(1, "累加") {
        @Override public long update(long value, long target) {
            return Math.addExact(value, target);
        }
    };

    /** 直接替换 */
    public static final UpdateType Replace = new UpdateType(2, "替换") {
        @Override public long update(long value, long target) {
            return target;
        }
    };

    /** 取最大值 */
    public static final UpdateType Max = new UpdateType(3, "取最大值") {
        @Override public long update(long value, long target) {
            return Math.max(value, target);
        }
    };

    /** 取最小值 */
    public static final UpdateType Min = new UpdateType(4, "取最小值") {
        @Override public long update(long value, long target) {
            return Math.min(value, target);
        }
    };


    private final int code;
    private final String comment;

    public UpdateType(int code, String comment) {
        this.code = code;
        this.comment = comment;
        UpdateType old = static_map.put(code, this);
        AssertUtil.assertTrue(old == null, "重复定义进度变更方式 code=%d", code);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        UpdateType that = (UpdateType) o;
        return getCode() == that.getCode();
    }

    @Override public int hashCode() {
        return getCode();
    }

    public abstract long update(long value, long target);

}
