package wxdgaming.game.server.api.role;

import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;

/**
 * 获取player对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-18 19:42
 **/
public interface GetPlayerStrategy {

    RoleEntity roleEntity(long rid);

    Player getPlayer(long rid);

    void putCache(RoleEntity roleEntity);

    void save(RoleEntity roleEntity);
}
