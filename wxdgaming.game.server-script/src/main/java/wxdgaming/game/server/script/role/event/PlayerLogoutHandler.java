package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.event.OnLogout;
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
    @OnLogout
    public void onLogout(Player player) {
        log.info("玩家下线: {} {}", ThreadContext.context().queueName(), player);
        player.getStatus().addFlags(StatusConst.Offline);
        ClientSessionMapping clientSessionMapping = player.getClientSessionMapping();
        clientSessionMapping.setRid(0);
        clientSessionMapping.setClientSessionId(0);
        clientSessionMapping.setGatewayId(0);
        clientSessionMapping.setSession(null);
        player.setClientSessionMapping(null);
        dataCenterService.getOnlinePlayerGroup().remove(player.getUid());
        RoleEntity roleEntity = dataCenterService.roleEntity(player.getUid());
        dataCenterService.save(roleEntity);
    }

}
