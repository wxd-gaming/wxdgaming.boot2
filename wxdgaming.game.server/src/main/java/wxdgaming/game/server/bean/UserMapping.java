package wxdgaming.game.server.bean;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.PojoBase;
import wxdgaming.game.login.bean.UserDataVo;
import wxdgaming.game.message.global.MapBean;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;

import java.util.List;

/**
 * 用户映射
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 14:19
 **/
@Getter
@Setter
public class UserMapping {

    private final String account;
    private final int userHashCode;
    private transient final DataCenterService dataCenterService;
    private UserDataVo userDataVo;
    private String clientIp;
    private List<MapBean> clientParams;

    private int sid;
    private long rid;

    private transient SocketSession socketSession;

    public UserMapping(String account, DataCenterService dataCenterService) {
        this.account = account;
        this.userHashCode = this.account.hashCode();
        this.dataCenterService = dataCenterService;
    }

    public Player player() {
        if (rid == 0) return null;
        return dataCenterService.getPlayer(rid);
    }

    public ChannelFuture write(PojoBase message) {
        return socketSession.write(message);
    }

    public ChannelFuture writeAndFlush(PojoBase message) {
        return socketSession.writeAndFlush(message);
    }

    @Override public String toString() {
        return "UserMapping{account='%s', sid=%d, rid=%d}".formatted(account, sid, rid);
    }
}
