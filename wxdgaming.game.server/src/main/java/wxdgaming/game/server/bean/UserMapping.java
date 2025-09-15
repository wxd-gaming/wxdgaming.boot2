package wxdgaming.game.server.bean;

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
    private UserDataVo userDataVo;
    private String clientIp;
    private List<MapBean> clientParams;

    private int sid;
    private long rid;

    private transient DataCenterService dataCenterService;
    private transient SocketSession socketSession;

    public UserMapping(String account) {
        this.account = account;
    }

    public Player player() {
        return dataCenterService.getPlayer(rid);
    }

    public void write(PojoBase message) {
        socketSession.write(message);
    }

    public void writeAndFlush(PojoBase message) {
        socketSession.writeAndFlush(message);
    }

    @Override public String toString() {
        return "UserMapping{account='%s', sid=%d, rid=%d}".formatted(account, sid, rid);
    }
}
