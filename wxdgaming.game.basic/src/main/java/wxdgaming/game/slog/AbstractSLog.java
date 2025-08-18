package wxdgaming.game.slog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 角色日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:22
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractSLog extends ObjectBase {

    private int sid;
    private int curSid;

    public AbstractSLog(int sid, int curSid) {
        this.sid = sid;
        this.curSid = curSid;
    }

    public abstract String logType();

}
