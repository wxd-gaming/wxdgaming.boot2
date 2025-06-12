package wxdgaming.game.server.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.MysqlService2;
import wxdgaming.game.global.bean.role.PlayerSnap;
import wxdgaming.game.server.bean.role.Player;

/**
 * 全服的全局数据，公共数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 16:34
 **/
@Slf4j
@Getter
@Singleton
public class GlobalDbDataCenterService {

    final SqlDataHelper<?> globalDbHelper;
    final DataCenterService dataCenterService;

    @Inject
    public GlobalDbDataCenterService(MysqlService2 mysqlService2, DataCenterService dataCenterService) {
        this.globalDbHelper = mysqlService2;
        this.dataCenterService = dataCenterService;
    }

    @Start
    public void start(@Value(path = "sid") int sid) {

    }

    public PlayerSnap playerSnap(long uid) {
        SingletonLockUtil.lock(uid);
        try {
            PlayerSnap playerSnap = globalDbHelper.getCacheService().cacheIfPresent(PlayerSnap.class, uid);
            if (playerSnap == null) {
                Player player = dataCenterService.player(uid);
                playerSnap = player.toPlayerSnap();
                globalDbHelper.getCacheService().cache(PlayerSnap.class).putIfAbsent(uid, playerSnap);
            }
            return playerSnap;
        } finally {
            SingletonLockUtil.unlock(uid);
        }
    }

}
