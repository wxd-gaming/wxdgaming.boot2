package wxdgaming.game.server.script.role.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.AbstractRoleLog;

/**
 * 角色详情
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 11:23
 **/
@Getter
@Setter
public class RoleInfoSlog extends AbstractRoleLog {

    public RoleInfoSlog(Player player) {
        super(player);
    }

}
