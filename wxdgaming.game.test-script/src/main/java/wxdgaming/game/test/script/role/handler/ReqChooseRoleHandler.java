package wxdgaming.game.test.script.role.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.ann.ProtoRequest;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;
import wxdgaming.game.test.script.event.EventBus;
import wxdgaming.game.test.script.event.OnLogin;
import wxdgaming.game.test.script.event.OnLoginBefore;
import wxdgaming.game.test.script.role.message.ReqChooseRole;

import java.util.HashSet;

/**
 * 选择角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Singleton
public class ReqChooseRoleHandler {

    private final EventBus eventBus;
    private final DataCenterService dataCenterService;

    @Inject
    public ReqChooseRoleHandler(EventBus eventBus, DataCenterService dataCenterService) {
        this.eventBus = eventBus;
        this.dataCenterService = dataCenterService;
    }

    /** 选择角色 */
    @ProtoRequest
    public void reqChooseRole(SocketSession socketSession, ReqChooseRole req) {
        long rid = req.getRid();
        Integer sid = socketSession.attribute("sid");
        String account = socketSession.attribute("account");
        HashSet<Long> longs = dataCenterService.getAccount2RidsMap().get(sid, account);
        if (longs == null || !longs.contains(rid)) {
            /*选择角色错误*/
            log.error("sid={}, account={} 选择角色错误 角色id不存在：{}", sid, account, rid);
            return;
        }

        Player player = dataCenterService.player(rid);
        log.info("sid={}, {} 触发登录之前校验事件", sid, player);
        eventBus.post(OnLoginBefore.class, player);
        log.info("sid={}, {} 触发登录事件", sid, player);
        eventBus.post(OnLogin.class, player, 1, 1);
        log.info("sid={}, {} 选择角色成功", sid, player);
    }

}