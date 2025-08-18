package wxdgaming.game.server.script.role.log;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.AbstractRoleLog;

/**
 * 角色登录日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:46
 **/
@Getter
@Setter
public class RoleLvLog extends AbstractRoleLog {

    private String reason;

    public RoleLvLog(Player player, String reason) {
        super(player);
        this.reason = reason;
    }

    @Override public String logType() {
        return "rolelvlog";
    }

}
