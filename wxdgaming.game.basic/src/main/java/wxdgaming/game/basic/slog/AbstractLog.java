package wxdgaming.game.basic.slog;

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
public abstract class AbstractLog extends ObjectBase {

    public String logType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

}
