package wxdgaming.game.common.bean.slog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 角色日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:22
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractSlog extends AbstractLog {

    private int sid;
    private int curSid;

    public AbstractSlog(int sid, int curSid) {
        this.sid = sid;
        this.curSid = curSid;
    }

}
