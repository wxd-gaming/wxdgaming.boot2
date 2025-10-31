package wxdgaming.game.server.bean.slog;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.timer.MyClock;
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

    long roleCreateTime;
    String roleCreateDay;
    String online;
    int vipLv;
    /** 最好登录时间 */
    long loginCount;
    /** 最好登录时间 */
    long lastLoginTime;
    /** 最好退出登录时间 */
    long lastLogoutTime;
    /** 在线时长，单位秒 */
    long totalOnlineSeconds;

    public RoleInfoSlog(Player player, String online) {
        super(player);
        this.roleCreateTime = player.getCreateTime();
        this.roleCreateDay = MyClock.formatDate("yyyyMMdd", player.getCreateTime());
        this.online = online;
        this.vipLv = 1;
        this.loginCount = player.getOnlineInfo().getLoginCount();
        this.lastLoginTime = player.getOnlineInfo().getLastLoginTime();
        this.lastLogoutTime = player.getOnlineInfo().getLastLogoutTime();
        this.totalOnlineSeconds = player.getOnlineInfo().getOnlineTotalMills() / 1000;
    }

}
