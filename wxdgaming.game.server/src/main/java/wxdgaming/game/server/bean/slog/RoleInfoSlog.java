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

    long refreshTime;
    long roleCreateTime;
    String online;
    int vipLv;
    /** 最好登录时间 */
    long loginCount;
    /** 最好登录时间 */
    long lastLoginTime;
    /** 最好退出登录时间 */
    long lastLogoutTime;
    /** 在线毫秒数 */
    long totalOnlineMills;

    public RoleInfoSlog(Player player, String online) {
        super(player);
        this.refreshTime = System.currentTimeMillis();
        this.roleCreateTime = player.getCreateTime();
        this.online = online;
        this.vipLv = 1;
        this.loginCount = player.getOnlineInfo().getLoginCount();
        this.lastLoginTime = player.getOnlineInfo().getLastLoginTime();
        this.lastLogoutTime = player.getOnlineInfo().getLastLogoutTime();
        this.totalOnlineMills = player.getOnlineInfo().getOnlineTotalMills() / 1000;
    }

}
