package wxdgaming.game.test.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;

/**
 * 数据中心
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 16:34
 **/
@Slf4j
@Singleton
public class DataCenterService {

    final PgsqlService pgsqlService;

    @Inject
    public DataCenterService(PgsqlService pgsqlService) {
        this.pgsqlService = pgsqlService;
    }

    public Player player(long uid) {
        return pgsqlService.getCacheService().cache(Player.class, uid);
    }

    public BagPack bagPack(long uid) {
        return pgsqlService.getCacheService().cache(BagPack.class, uid);
    }

    public TaskPack taskPack(long uid) {
        return pgsqlService.getCacheService().cache(TaskPack.class, uid);
    }

}
