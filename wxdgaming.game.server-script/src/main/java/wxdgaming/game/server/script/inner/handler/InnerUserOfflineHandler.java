package wxdgaming.game.server.script.inner.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.inner.InnerUserOffline;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLogout;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 玩家离线
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class InnerUserOfflineHandler extends HoldRunApplication {

    private final DataCenterService dataCenterService;
    private final ClientSessionService clientSessionService;

    @Inject
    public InnerUserOfflineHandler(DataCenterService dataCenterService, ClientSessionService clientSessionService) {
        this.dataCenterService = dataCenterService;
        this.clientSessionService = clientSessionService;
    }

    /** 玩家离线 */
    @ProtoRequest
    public void innerUserOffline(SocketSession socketSession, InnerUserOffline req) {
        long clientSessionId = req.getClientSessionId();
        String account = req.getAccount();
        log.info("网关转发玩家离线 {}", req);
        ClientSessionMapping remove = clientSessionService.getAccountMappingMap().get(account);
        if (remove != null) {
            Player player = remove.getPlayer();
            if (player != null) {
                runApplication.executeMethodWithAnnotatedException(OnLogout.class, player);
                player.setClientSessionMapping(null);
            }
            remove.setSession(null);
            remove.setRid(0);
            remove.setSid(0);
            remove.setPlayer(null);
            remove.setClientSessionId(0);
        }
    }

}