package wxdgaming.game.test.module.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.format.HexId;
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
@Getter
@Singleton
public class DataCenterService {

    final PgsqlService pgsqlService;
    HexId itemHexid;

    @Inject
    public DataCenterService(PgsqlService pgsqlService) {
        this.pgsqlService = pgsqlService;
    }

    @Start
    public void start(@Value(path = "sid") int sid) {
        itemHexid = new HexId(sid);
    }

    public Player player(long uid) {
        return pgsqlService.getCacheService().cacheIfPresent(Player.class, uid);
    }

    public BagPack bagPack(long uid) {
        BagPack bagPack = pgsqlService.getCacheService().cacheIfPresent(BagPack.class, uid);
        if (bagPack == null) {
            bagPack = new BagPack();
            bagPack.setUid(uid);
            BagPack old = pgsqlService.getCacheService().cache(BagPack.class).putIfAbsent(uid, bagPack);
            if (old != null) {
                bagPack = old;
            }
        }
        return bagPack;
    }

    public TaskPack taskPack(long uid) {
        TaskPack taskPack = pgsqlService.getCacheService().cacheIfPresent(TaskPack.class, uid);
        if (taskPack == null) {
            taskPack = new TaskPack();
            taskPack.setUid(uid);
            TaskPack old = pgsqlService.getCacheService().cache(TaskPack.class).putIfAbsent(uid, taskPack);
            if (old != null) {
                taskPack = old;
            }
        }
        return taskPack;
    }

}
