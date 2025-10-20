package wxdgaming.game.server.script.recharge.slog;

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
public class RoleRechargeSlog extends AbstractRoleSlog {

    private String cpOrderId;
    private String spOrderId;
    /** 单位：分 */
    private int amount;
    /** 商品id */
    private int productId;
    /** 商品名称 */
    private String productName;
    /** 备注字符串 */
    private String comment;

    public RoleRechargeSlog(Player player) {
        super(player);
    }

    @Override public String logType() {
        return "rolerechargeslog";
    }

}
