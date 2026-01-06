package wxdgaming.game.server.bean.reason;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 原因封装
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-04 09:00
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ReasonDTO extends ObjectBase {

    public static ReasonDTO of(ReasonConst reason, Object... args) {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setSerialNumber(System.nanoTime());
        reasonDTO.setReasonConst(reason);
        reasonDTO.setArgs(args);
        return reasonDTO;
    }

    /** 原因类型 */
    @JSONField(ordinal = 1, name = "原因")
    private ReasonConst reasonConst;
    /** 并非唯一 */
    @JSONField(ordinal = 2, name = "流水")
    private long serialNumber;
    /** 多参数 */
    @JSONField(ordinal = 3, name = "args")
    private Object[] args;
    /** 最终拼装的 */
    @JSONField(serialize = false)
    private transient String reasonText;

    public ReasonDTO() {
    }

    public String getReasonText() {
        if (reasonText == null) {
            reasonText = this.toJSONString();
        }
        return reasonText;
    }

    public ReasonDTO copyFrom(Object... appendArgs) {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setSerialNumber(serialNumber);
        reasonDTO.setReasonConst(reasonConst);
        reasonDTO.setArgs(ArrayUtils.addAll(args, appendArgs));
        return reasonDTO;
    }

    @Override public String toString() {
        return getReasonText();
    }

}
