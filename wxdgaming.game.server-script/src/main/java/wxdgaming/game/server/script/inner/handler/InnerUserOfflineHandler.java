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
import wxdgaming.game.server.module.drive.PlayerDriveService;

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
    private final PlayerDriveService playerDriveService;
    private final ClientSessionService clientSessionService;

    @Inject
    public InnerUserOfflineHandler(DataCenterService dataCenterService, PlayerDriveService playerDriveService, ClientSessionService clientSessionService) {
        this.dataCenterService = dataCenterService;
        this.playerDriveService = playerDriveService;
        this.clientSessionService = clientSessionService;
    }

    /** 玩家离线 */
    @ProtoRequest
    public void innerUserOffline(SocketSession socketSession, InnerUserOffline req) {
        long clientSessionId = req.getClientSessionId();
        String account = req.getAccount();
        log.info("网关转发玩家离线 {}", req);
        ClientSessionMapping clientSessionMapping = clientSessionService.getAccountMappingMap().get(account);
        if (clientSessionMapping != null) {
            Player player = dataCenterService.player(clientSessionMapping.getRid());
            Runnable afterRunnable = () -> {
                clientSessionMapping.setClientSessionId(0);
                clientSessionMapping.setGatewayId(0);
                clientSessionMapping.setSession(null);
                clientSessionMapping.setRid(0);
                clientSessionMapping.setSid(0);
            };
            if (player != null) {
                playerDriveService.executor(player, () -> {
                    runApplication.executeMethodWithAnnotatedException(OnLogout.class, player);
                    player.setClientSessionMapping(null);
                    afterRunnable.run();
                });
            } else {
                afterRunnable.run();
            }
        }
    }

}