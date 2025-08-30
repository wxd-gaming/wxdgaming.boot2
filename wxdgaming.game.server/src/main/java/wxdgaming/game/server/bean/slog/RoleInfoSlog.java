package wxdgaming.game.server.bean.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;

/**
 * 角色详情
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 11:23
 **/
@Getter
@Setter
public class RoleInfoSlog extends AbstractRoleSlog {

    private String online;
    private int vipLv;

    public RoleInfoSlog(Player player, String online, int vipLv) {
        super(player);
        this.online = online;
        this.vipLv = vipLv;
    }

}
