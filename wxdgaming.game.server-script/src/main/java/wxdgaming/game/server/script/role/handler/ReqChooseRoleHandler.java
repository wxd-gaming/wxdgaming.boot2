package wxdgaming.game.server.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.ThreadParam;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.global.bean.role.PlayerSnap;
import wxdgaming.game.message.role.ReqChooseRole;
import wxdgaming.game.message.role.ResChooseRole;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.event.OnLogout;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.data.GlobalDbDataCenterService;
import wxdgaming.game.server.module.drive.PlayerDriveService;

import java.util.HashSet;

/**
 * 选择角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqChooseRoleHandler extends HoldRunApplication {

    final DataCenterService dataCenterService;
    final GlobalDbDataCenterService globalDbDataCenterService;
    final PlayerDriveService playerDriveService;

    @Inject
    public ReqChooseRoleHandler(DataCenterService dataCenterService, GlobalDbDataCenterService globalDbDataCenterService, PlayerDriveService playerDriveService) {
        this.dataCenterService = dataCenterService;
        this.globalDbDataCenterService = globalDbDataCenterService;
        this.playerDriveService = playerDriveService;
    }

    /** 选择角色 */
    @ProtoRequest
    public void reqChooseRole(SocketSession socketSession, ReqChooseRole req, @ThreadParam(path = "clientSessionMapping") ClientSessionMapping clientSessionMapping) {
        long rid = req.getRid();
        log.info("选择角色请求:{}, clientSession={}", req, clientSessionMapping);
        Integer sid = clientSessionMapping.getSid();
        String account = clientSessionMapping.getAccount();
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs == null || !longs.contains(rid)) {
            /*选择角色错误*/
            log.error("sid={}, account={} 选择角色错误 角色id不存在：{}", sid, account, rid);
            return;
        }

        Player player = dataCenterService.getPlayer(rid);
        playerDriveService.executor(player, () -> {
            if (clientSessionMapping.getRid() > 0 && clientSessionMapping.getRid() != player.getUid()) {
                /*角色切换*/
                log.info("sid={}, account={} 角色切换 rid={} -> {}", sid, account, clientSessionMapping.getRid(), player.getUid());
                runApplication.executeMethodWithAnnotatedException(OnLogout.class, player);
            }

            PlayerSnap playerSnap = globalDbDataCenterService.playerSnap(player.getUid());
            player.buildPlayerSnap(playerSnap);

            player.setClientSessionMapping(clientSessionMapping);
            clientSessionMapping.setRid(player.getUid());

            dataCenterService.getOnlinePlayerGroup().put(player.getUid(), clientSessionMapping.getClientSessionId());

            /*绑定*/
            log.info("sid={}, {} 触发登录之前校验事件", sid, player);
            runApplication.executeMethodWithAnnotatedException(OnLoginBefore.class, player);
            ResChooseRole resChooseRole = new ResChooseRole();
            resChooseRole.setRid(rid);
            clientSessionMapping.forwardMessage(player, resChooseRole);
            log.info("sid={}, {} 触发登录事件", sid, player);
            runApplication.executeMethodWithAnnotatedException(OnLogin.class, player, 1, 1);
            log.info("sid={}, {} 选择角色成功", sid, player);
        });
    }

}