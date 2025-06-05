package wxdgaming.game.core;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 原因封装
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-04 09:00
 **/
@Getter
@Setter
public class ReasonArgs extends ObjectBase {

    public static ReasonArgs of(Reason reason, Object... args) {
        ReasonArgs reasonArgs = new ReasonArgs();
        reasonArgs.setSerialNumber(System.nanoTime());
        reasonArgs.setReason(reason);
        reasonArgs.setArgs(args);
        return reasonArgs;
    }

    /** 原因类型 */
    @JSONField(ordinal = 1, name = "原因")
    private Reason reason;
    /** 并非唯一 */
    @JSONField(ordinal = 2, name = "流水")
    private long serialNumber;
    /** 多参数 */
    @JSONField(ordinal = 3, name = "args")
    private Object[] args;
    /** 最终拼装的 */
    @JSONField(serialize = false)
    private transient String reasonText;

    public ReasonArgs() {
    }

    public String getReasonText() {
        if (reasonText == null) {
            reasonText = this.toJSONString();
        }
        return reasonText;
    }

    public ReasonArgs copyFrom(Object... appendArgs) {
        ReasonArgs reasonArgs = new ReasonArgs();
        reasonArgs.setSerialNumber(serialNumber);
        reasonArgs.setReason(reason);
        reasonArgs.setArgs(Objects.merge(args, appendArgs, appendArgs.length));
        return reasonArgs;
    }

    @Override public String toString() {
        return getReasonText();
    }

}
