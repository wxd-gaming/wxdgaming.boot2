package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.event.EventConst;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 角色创建事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 19:51
 **/
@Slf4j
@Component
public class PlayerLogoutHandler {

    private final DataCenterService dataCenterService;

    public PlayerLogoutHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @EventListener
    public void onLogout(EventConst.LogoutPlayerEvent event) {
        Player player = event.player();
        player.getStatus().addFlags(StatusConst.Offline);
        player.getOnlineInfo().setLastLogoutTime(MyClock.millis());
        UserMapping clientSessionMapping = player.getUserMapping();
        SocketSession socketSession = clientSessionMapping.getSocketSession();
        log.info("玩家下线: {} {}, \n{}", ThreadContext.context().queueName(), player, socketSession.flowString());
        clientSessionMapping.setRid(0);
        clientSessionMapping.setSocketSession(null);
        player.setUserMapping(null);
        RoleEntity roleEntity = dataCenterService.roleEntity(player.getUid());
        dataCenterService.save(roleEntity);
    }

}
