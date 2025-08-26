package wxdgaming.game.server.script.role.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.slog.AbstractRoleSlog;

/**
 * 角色登录日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:46
 **/
@Getter
@Setter
public class RoleLvSlog extends AbstractRoleSlog {

    private String reason;

    public RoleLvSlog(Player player, String reason) {
        super(player);
        this.reason = reason;
    }

    @Override public String logType() {
        return "rolelvlog";
    }

}
