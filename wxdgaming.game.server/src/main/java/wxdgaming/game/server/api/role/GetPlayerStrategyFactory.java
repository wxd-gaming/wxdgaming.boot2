package wxdgaming.game.server.api.role;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;

/**
 * 获取player对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-18 19:42
 **/
@Slf4j
public class GetPlayerStrategyFactory implements GetPlayerStrategy {

    private final GetPlayerStrategy playerProxy;

    public GetPlayerStrategyFactory(GetPlayerStrategy getPlayer) {
        this.playerProxy = getPlayer;
    }

    @Override public RoleEntity roleEntity(long rid) {
        return playerProxy.roleEntity(rid);
    }

    @Override public Player getPlayer(long rid) {
        return playerProxy.getPlayer(rid);
    }

    @Override public void putCache(RoleEntity roleEntity) {
        this.playerProxy.putCache(roleEntity);
    }

    @Override public void save(RoleEntity roleEntity) {
        this.playerProxy.save(roleEntity);
    }
}
