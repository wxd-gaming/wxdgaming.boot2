package wxdgaming.boot2.core.lang.condition;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.util.AssertUtil;

import java.io.Serializable;
import java.util.Objects;

/**
 * 完成条件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-10 15:36
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Condition extends ObjectBase implements Serializable {

    /** 条件1 */
    private final Serializable k1;
    /** 条件2 */
    private final Serializable k2;
    /** 条件3 */
    private final Serializable k3;
    /** 当前完成条件变更方案 */
    private final ConditionUpdatePolicy update;
    /** 目标完成条件 */
    private final long target;

    public Condition(Serializable k1, long target) {
        this(k1, "0", "0", ConditionUpdatePolicyConst.Add, target);
    }

    public Condition(Serializable k1, ConditionUpdatePolicy update, long target) {
        this(k1, "0", "0", update, target);
    }

    /** fastjson */
    @JSONCreator
    public Condition(@JSONField(name = "k1") Serializable k1,
                     @JSONField(name = "k2") Serializable k2,
                     @JSONField(name = "k3") Serializable k3,
                     @JSONField(name = "update") ConditionUpdatePolicyConst update,
                     @JSONField(name = "target") long target) {
        this(k1, k2, k3, (ConditionUpdatePolicy) update, target);
    }

    public Condition(Serializable k1,
                     Serializable k2,
                     Serializable k3,
                     ConditionUpdatePolicy update,
                     long target) {
        AssertUtil.assertTrue(k1 != null, "条件1不能为空");
        this.k1 = k1;
        this.k2 = k2 == null ? "0" : k2;
        this.k3 = k3 == null ? "0" : k3;
        this.update = update == null ? ConditionUpdatePolicyConst.Add : update;
        this.target = target;
    }

    public boolean equals(Serializable k1, Serializable k2, Serializable k3) {
        return this.k1.equals(k1)
               && (this.k2 == null || "0".equals(this.k2) || Objects.equals(this.k2, k2))
               && (this.k3 == null || "0".equals(this.k3) || Objects.equals(this.k3, k3));
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;
        return equals(condition.getK1(), condition.getK2(), condition.getK3());
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(getK1());
        result = 31 * result + Objects.hashCode(getK2());
        result = 31 * result + Objects.hashCode(getK3());
        return result;
    }

    public Condition copy() {
        return new Condition(this.k1, this.k2, this.k3, this.update, this.target);
    }

    public Condition copy(long target) {
        return new Condition(this.k1, this.k2, this.k3, this.update, target);
    }

}
