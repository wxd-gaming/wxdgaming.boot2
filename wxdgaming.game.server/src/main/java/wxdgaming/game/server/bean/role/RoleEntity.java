package wxdgaming.game.server.bean.role;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 角色数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 14:43
 **/
@Getter
@Setter
@Accessors(chain = true)
@DbTable(tableName = "role")
public class RoleEntity extends EntityLongUID {

    private long lastUpdateTime;
    @DbColumn(index = true)
    int sid;
    @DbColumn(index = true, length = 18)
    String name;
    @DbColumn(index = true)
    int lv;
    @DbColumn(index = true)
    int vipLv;
    @DbColumn(index = true, length = 64)
    String account;
    @DbColumn(index = true)
    boolean del;
    /** 最好登录时间 */
    @DbColumn()
    long loginCount;
    /** 最好登录时间 */
    @DbColumn(index = true)
    long lastLoginTime;
    /** 最好退出登录时间 */
    @DbColumn(index = true)
    long lastLogoutTime;
    /** 在线毫秒数 */
    @DbColumn(index = true)
    long totalOnlineMills;
    /** 角色数据 */
    @DbColumn(length = Integer.MAX_VALUE, columnType = ColumnType.String)
    private Player player;

    @Override public void saveRefresh() {
        lastUpdateTime = System.currentTimeMillis();
        if (getUid() == 0) {
            setUid(player.getUid());
        }
        sid = player.getSid();
        name = player.getName();
        account = player.getAccount();
        del = player.isDel();
        loginCount = player.getOnlineInfo().getLoginCount();
        lastLoginTime = player.getOnlineInfo().getLastLoginTime();
        lastLogoutTime = player.getOnlineInfo().getLastLogoutTime();
        totalOnlineMills = player.getOnlineInfo().getOnlineTotalMills();
    }

    @Override public RoleEntity setUid(long uid) {
        super.setUid(uid);
        return this;
    }

}
