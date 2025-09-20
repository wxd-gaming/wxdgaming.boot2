package wxdgaming.game.login.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

/**
 * 内置的服务器信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 15:23
 **/
@Getter
@Setter
@DbTable
public class ServerInfoEntity extends Entity implements Cloneable {

    @DbColumn(key = true)
    private int serverId;
    private int mainId;
    private int gid;
    /** 0 无限制, 1 白名单显示, 2 永久隐藏 */
    private int showLevel;
    private String name;
    /** 外网同步地址 */
    private String host;
    /** 内外同步的地址 */
    private String innerHost;
    private int port;
    private int httpPort;
    /** 开启时间 */
    private long openTime;
    /** 维护时间 */
    private long maintenanceTime;
    private long lastSyncTime;
    private int status;
    private int maxOnlineSize = 1000;
    @DbColumn(ignore = true)
    private int onlineSize = 0;

    public int free() {
        return maxOnlineSize - onlineSize;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfoEntity that = (ServerInfoEntity) o;
        return getGid() == that.getGid() && getServerId() == that.getServerId();
    }

    @Override public int hashCode() {
        int result = getGid();
        result = 31 * result + getServerId();
        return result;
    }

    @Override public ServerInfoEntity clone() {
        return (ServerInfoEntity) super.clone();
    }
}
