package wxdgaming.game.core;

import lombok.Getter;
import lombok.Setter;
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

    private long serialNumber;
    private Reason reason;
    private Object[] args;
    private String msg;

    public ReasonArgs() {
    }

    public ReasonArgs(Reason reason, Object... args) {
        this.serialNumber = System.nanoTime();
        this.reason = reason;
        this.args = args;
    }

    public String getMsg() {
        if (msg == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("原因：").append(reason.toString()).append(", 流水号：").append(serialNumber);
            for (Object arg : args) {
                builder.append(", ").append(String.valueOf(arg));
            }
            msg = builder.toString();
        }
        return msg;
    }

    @Override public String toString() {
        return getMsg();
    }

}
