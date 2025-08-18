package wxdgaming.game.server.api.role.impl;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.server.api.role.GetPlayerStrategy;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;

/**
 * 通过数据库获取player
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-18 20:39
 **/
@Slf4j
public class DatabaseGetPlayerStrategy implements GetPlayerStrategy {

    private final SqlDataHelper sqlDataHelper;

    public DatabaseGetPlayerStrategy(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Override public RoleEntity roleEntity(long rid) {
        return sqlDataHelper.getCacheService().cacheIfPresent(RoleEntity.class, rid);
    }

    @Override public Player getPlayer(long rid) {
        RoleEntity roleEntity = roleEntity(rid);
        if (roleEntity != null) {
            return roleEntity.getPlayer();
        }
        return null;
    }

    @Override public void putCache(RoleEntity roleEntity) {
        sqlDataHelper.getCacheService().cache(RoleEntity.class).put(roleEntity.getUid(), roleEntity);
    }

    @Override public void save(RoleEntity roleEntity) {
        sqlDataHelper.getDataBatch().save(roleEntity);
    }
}
