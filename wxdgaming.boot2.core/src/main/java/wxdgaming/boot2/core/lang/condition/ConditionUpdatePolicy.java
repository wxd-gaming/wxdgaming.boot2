package wxdgaming.boot2.core.lang.condition;

/**
 * 更新策略
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-02 10:09
 */
public interface ConditionUpdatePolicy {

    /** 备注名称 */
    String getComment();

    /** 更新操作 */
    long update(long value, long target);
}
