package wxdgaming.game.test.module.data;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.starter.batis.sql.SqlQueryResult;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.bean.task.TaskPack;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

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
    HexId hexid;
    HexId itemHexid;
    /** key:serverID, key:account, value: 角色id列表 */
    final ConcurrentTable<Integer, String, HashSet<Long>> account2RidsMap = new ConcurrentTable<>();
    /** 角色名字和id的映射 key:name, value:roleId */
    final ConcurrentHashMap<String, Long> name2RidMap = new ConcurrentHashMap<>();
    /** 角色id和名字的映射 key:roleId, value:name */
    final ConcurrentHashMap<Long, String> rid2NameMap = new ConcurrentHashMap<>();

    @Inject
    public DataCenterService(PgsqlService pgsqlService) {
        this.pgsqlService = pgsqlService;
    }

    @Start
    public void start(@Value(path = "sid") int sid) {
        hexid = new HexId(sid);
        itemHexid = new HexId(sid);
        String sql = "SELECT uid,sid,name,account FROM player where del=?";
        try (SqlQueryResult sqlQueryResult = pgsqlService.queryResultSet(sql, false)) {
            while (sqlQueryResult.hasNext()) {
                JSONObject row = sqlQueryResult.row();
                String account = row.getString("account");
                int roleSid = row.getIntValue("sid");
                long roleId = row.getLongValue("uid");
                String name = row.getString("name");
                account2RidsMap.computeIfAbsent(roleSid, account, k -> new HashSet<>()).add(roleId);
                name2RidMap.put(name, roleId);
                rid2NameMap.put(roleId, name);
            }
        }
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
