package wxdgaming.game.server.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqChooseRole;
import wxdgaming.game.message.role.ResChooseRole;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.module.data.DataCenterService;

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

    private final DataCenterService dataCenterService;

    @Inject
    public ReqChooseRoleHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    /** 选择角色 */
    @ProtoRequest
    public void reqChooseRole(SocketSession socketSession, ReqChooseRole req) {
        long rid = req.getRid();
        ClientSessionMapping clientSessionMapping = ThreadContext.context("clientSessionMapping");
        log.info("选择角色请求:{}, clientSessionId={}", req, clientSessionMapping);
        Integer sid = clientSessionMapping.getSid();
        String account = clientSessionMapping.getAccount();
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs == null || !longs.contains(rid)) {
            /*选择角色错误*/
            log.error("sid={}, account={} 选择角色错误 角色id不存在：{}", sid, account, rid);
            return;
        }

        Player player = dataCenterService.player(rid);
        player.setClientSessionMapping(clientSessionMapping);
        clientSessionMapping.setRid(player.getUid());
        clientSessionMapping.setPlayer(player);

        dataCenterService.getOnlinePlayerGroup().put(player.getUid(), clientSessionMapping.getClientSessionId());

        /*绑定*/
        log.info("sid={}, {} 触发登录之前校验事件", sid, player);
        runApplication.executeMethodWithAnnotatedException(OnLoginBefore.class, player);
        ResChooseRole resChooseRole = new ResChooseRole();
        clientSessionMapping.forwardMessage(resChooseRole);
        log.info("sid={}, {} 触发登录事件", sid, player);
        runApplication.executeMethodWithAnnotatedException(OnLogin.class, player, 1, 1);
        log.info("sid={}, {} 选择角色成功", sid, player);

    }

}