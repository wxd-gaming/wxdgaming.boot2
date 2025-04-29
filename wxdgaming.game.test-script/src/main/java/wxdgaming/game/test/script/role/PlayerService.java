package wxdgaming.game.test.script.role;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.role.message.ResLogin;
import wxdgaming.game.test.script.role.message.RoleBean;

import java.util.HashSet;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 11:44
 **/
@Slf4j
@Singleton
public class PlayerService extends HoldRunApplication {

    final DataCenterService dataCenterService;

    @Inject
    public PlayerService(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }


    public void sendPlayerList(SocketSession socketSession, Integer sid, String account) {
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        ResLogin resLogin = new ResLogin();
        if (longs != null) {
            for (Long rid : longs) {
                Player player = dataCenterService.player(rid);
                RoleBean roleBean = new RoleBean().setRid(rid).setName(player.getName()).setLevel(player.getLevel());
                resLogin.getRoles().add(roleBean);
            }
        }
        socketSession.write(resLogin);
        log.info("{} {} 发送角色列表:{}", sid, account, resLogin);
    }

}
