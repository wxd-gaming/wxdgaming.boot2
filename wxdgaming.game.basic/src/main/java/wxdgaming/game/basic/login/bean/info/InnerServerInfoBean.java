package wxdgaming.game.basic.login.bean.info;

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
public class InnerServerInfoBean extends Entity implements Cloneable {

    @DbColumn(key = true)
    private int serverId;
    private int mainId;
    private int gid;
    private String name;
    private String host;
    private int port;
    private int httpPort;
    private long lastSyncTime;
    private int status;
    private int maxOnlineSize = 1000;
    private int onlineSize = 0;

    public int free() {
        return maxOnlineSize - onlineSize;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        InnerServerInfoBean that = (InnerServerInfoBean) o;
        return getGid() == that.getGid() && getServerId() == that.getServerId();
    }

    @Override public int hashCode() {
        int result = getGid();
        result = 31 * result + getServerId();
        return result;
    }

    @Override public InnerServerInfoBean clone() {
        return (InnerServerInfoBean) super.clone();
    }
}
