package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.OnLogin;
import wxdgaming.game.test.event.OnLoginBefore;
import wxdgaming.game.test.event.OnLogout;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.role.message.ReqChooseRole;
import wxdgaming.game.test.script.role.message.ResChooseRole;

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
        Integer sid = socketSession.bindData("sid");
        String account = socketSession.bindData("account");
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs == null || !longs.contains(rid)) {
            /*选择角色错误*/
            log.error("sid={}, account={} 选择角色错误 角色id不存在：{}", sid, account, rid);
            return;
        }

        Player player = dataCenterService.player(rid);
        player.setSocketSession(socketSession);
        socketSession.getChannel().closeFuture().addListener(future -> {
            if (Objects.equals(socketSession, player.getSocketSession())) {
                log.info("sid={}, {} 触发登出事件", sid, player);
                runApplication.executeMethodWithAnnotatedException(OnLogout.class, player);
            }
        });
        /*绑定*/
        socketSession.bindData("player", player);
        log.info("sid={}, {} 触发登录之前校验事件", sid, player);
        runApplication.executeMethodWithAnnotatedException(OnLoginBefore.class, player);
        ResChooseRole resChooseRole = new ResChooseRole();
        socketSession.write(resChooseRole);
        log.info("sid={}, {} 触发登录事件", sid, player);
        runApplication.executeMethodWithAnnotatedException(OnLogin.class, player, 1, 1);
        log.info("sid={}, {} 选择角色成功", sid, player);
        dataCenterService.getOnlinePlayerGroup().add(socketSession.getChannel());

    }

}