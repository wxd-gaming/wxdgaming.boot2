package wxdgaming.game.server.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.event.OnLogout;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Singleton
public class PlayerLogoutHandler {

    private final DataCenterService dataCenterService;

    @Inject
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
