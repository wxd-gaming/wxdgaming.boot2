package wxdgaming.game.server.bean.slog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.slog.AbstractSLog;

/**
 * 角色日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:22
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractRoleLog extends AbstractSLog {

    private String openId;
    private String account;
    private String platform;
    private String channel;
    private long roleId;
    private String roleName;
    private int lv;

    public AbstractRoleLog(Player player) {
        this.account = player.getAccount();
        this.platform = player.getPlatform();
        this.setSid(player.getSid());
        this.roleId = player.getUid();
        this.roleName = player.getName();
        this.lv = player.getLevel();
    }

    public abstract String logType();

}
