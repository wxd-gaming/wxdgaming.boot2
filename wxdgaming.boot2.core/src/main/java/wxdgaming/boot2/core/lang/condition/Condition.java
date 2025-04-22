package wxdgaming.boot2.core.lang.condition;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

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
    private final UpdateType updateType;

    public Condition(Serializable k1, Serializable k2, Serializable k3, UpdateType updateType) {
        this.k1 = String.valueOf(k1);
        this.k2 = String.valueOf(k2);
        this.k3 = String.valueOf(k3);
        this.updateType = updateType;
    }

    public boolean equals(Serializable k1, Serializable k2, Serializable k3) {
        return this.k1.equals(k1) && ("0".equals(this.k2) || this.k2.equals(k2)) && ("0".equals(this.k3) || this.k3.equals(k3));
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;
        return Objects.equals(getK1(), condition.getK1())
               && Objects.equals(getK2(), condition.getK2())
               && Objects.equals(getK3(), condition.getK3())
               && Objects.equals(getUpdateType(), condition.getUpdateType());
    }

    @Override public int hashCode() {
        int result = Objects.hashCode(getK1());
        result = 31 * result + Objects.hashCode(getK2());
        result = 31 * result + Objects.hashCode(getK3());
        result = 31 * result + Objects.hashCode(getUpdateType());
        return result;
    }

    public Condition copy() {
        return new Condition(this.k1, this.k2, this.k3, this.updateType);
    }

}
