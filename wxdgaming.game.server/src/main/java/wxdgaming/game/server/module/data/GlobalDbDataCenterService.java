package wxdgaming.game.server.module.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.server.entity.role.PlayerSnap;
import wxdgaming.game.server.bean.role.Player;

/**
 * 全服的全局数据，公共数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 16:34
 **/
@Slf4j
@Getter
@Service
public class GlobalDbDataCenterService {

    final SqlDataHelper globalDbHelper;
    final DataCenterService dataCenterService;

    public GlobalDbDataCenterService(@Qualifier("db.pgsql-second") SqlDataHelper globalDbHelper, DataCenterService dataCenterService) {
        this.globalDbHelper = globalDbHelper;
        this.dataCenterService = dataCenterService;
    }

    public PlayerSnap playerSnap(long uid) {
        SingletonLockUtil.lock(uid);
        try {
            PlayerSnap playerSnap = globalDbHelper.getCacheService().cacheIfPresent(PlayerSnap.class, uid);
            if (playerSnap == null) {
                Player player = dataCenterService.getPlayer(uid);
                playerSnap = player.toPlayerSnap();
                globalDbHelper.getCacheService().cache(PlayerSnap.class).putIfAbsent(uid, playerSnap);
            }
            return playerSnap;
        } finally {
            SingletonLockUtil.unlock(uid);
        }
    }

}
