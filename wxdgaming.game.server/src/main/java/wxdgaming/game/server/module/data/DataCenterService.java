package wxdgaming.game.server.module.data;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.core.keywords.KeywordsMapping;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryResult;
import wxdgaming.boot2.starter.net.module.inner.RpcService;
import wxdgaming.game.server.api.role.GetPlayerStrategyFactory;
import wxdgaming.game.server.api.role.GetPlayerStrategy;
import wxdgaming.game.server.api.role.impl.DatabaseGetPlayerStrategy;
import wxdgaming.game.server.api.role.impl.RpcGetPlayerStrategy;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;

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
public class DataCenterService extends HoldRunApplication implements GetPlayerStrategy {

    HexId hexid;
    HexId itemHexid;
    HexId mailHexid;
    HexId buffHexid;
    /** key:serverID, key:account, value: 角色id列表 */
    final ConcurrentTable<Integer, String, HashSet<Long>> account2RidsMap = new ConcurrentTable<>();
    /** 角色名字和id的映射 key:name, value:roleId */
    final ConcurrentHashMap<String, Long> name2RidMap = new ConcurrentHashMap<>();
    /** 角色id和名字的映射 key:roleId, value:name */
    final ConcurrentHashMap<Long, String> rid2NameMap = new ConcurrentHashMap<>();
    /** key:{@link Player#getUid()}, value:clientSessionId */
    final ConcurrentHashMap<Long, Long> onlinePlayerGroup = new ConcurrentHashMap<>();
    final KeywordsMapping keywordsMapping = new KeywordsMapping();

    GetPlayerStrategyFactory getPlayerStrategyFactory;

    @Inject
    public DataCenterService() {
    }

    @Start
    public void start(@Named("sid") int sid, @Named("serverType") int serverType) {
        hexid = new HexId(sid);
        itemHexid = new HexId(sid);
        mailHexid = new HexId(sid);
        buffHexid = new HexId(sid);
        if (serverType <= 1) {
            SqlDataHelper sqlDataHelper = runApplication.getInstance(SqlDataHelper.class);
            getPlayerStrategyFactory = new GetPlayerStrategyFactory(new DatabaseGetPlayerStrategy(sqlDataHelper));
            String sql = "SELECT uid,sid,name,account FROM role where del=?";
            try (SqlQueryResult sqlQueryResult = sqlDataHelper.queryResultSet(sql, false)) {
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
        } else {
            RpcService rpcService = runApplication.getInstance(RpcService.class);
            ClientSessionService clientSessionService = runApplication.getInstance(ClientSessionService.class);
            GlobalDbDataCenterService globalDbDataCenterService = runApplication.getInstance(GlobalDbDataCenterService.class);
            getPlayerStrategyFactory = new GetPlayerStrategyFactory(new RpcGetPlayerStrategy(rpcService, clientSessionService, globalDbDataCenterService));
        }
    }

    @Override public RoleEntity roleEntity(long rid) {
        return getPlayerStrategyFactory.roleEntity(rid);
    }

    @Override public Player getPlayer(long rid) {
        return getPlayerStrategyFactory.getPlayer(rid);
    }

    @Override public void putCache(RoleEntity roleEntity) {
        getPlayerStrategyFactory.putCache(roleEntity);
    }

    @Override public void save(RoleEntity roleEntity) {
        getPlayerStrategyFactory.putCache(roleEntity);
    }
}
