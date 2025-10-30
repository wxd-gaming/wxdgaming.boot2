package wxdgaming.game.server.module.data;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentDataBinding;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.format.HexId;
import wxdgaming.boot2.core.keywords.KeywordsMapping;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryResult;
import wxdgaming.boot2.starter.net.SessionGroup;
import wxdgaming.game.message.role.ResKick;
import wxdgaming.game.server.GameServerProperties;
import wxdgaming.game.server.api.role.GetPlayerStrategy;
import wxdgaming.game.server.api.role.GetPlayerStrategyFactory;
import wxdgaming.game.server.api.role.impl.DatabaseGetPlayerStrategy;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据中心
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 16:34
 **/
@Slf4j
@Getter
@Service
public class DataCenterService extends HoldApplicationContext implements GetPlayerStrategy {

    final GameServerProperties gameServerProperties;

    final HexId hexid;
    final HexId itemHexid;
    final HexId mailHexid;
    final HexId buffHexid;
    final ConcurrentHashMap<String, UserMapping> userMappingMap = new ConcurrentHashMap<>();
    /** key:serverID, key:account, value: 角色id列表 */
    final ConcurrentTable<Integer, String, HashSet<Long>> account2RidsMap = new ConcurrentTable<>();
    /** 角色名字和id的映射 key:name, value:roleId */
    final ConcurrentDataBinding<String, Long> nameRidMap = new ConcurrentDataBinding<>();
    final KeywordsMapping keywordsMapping = new KeywordsMapping();
    final SessionGroup onlinePlayers = new SessionGroup();

    GetPlayerStrategyFactory getPlayerStrategyFactory;

    public DataCenterService(GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
        int sid = gameServerProperties.getSid();

        hexid = new HexId(sid);
        itemHexid = new HexId(sid);
        mailHexid = new HexId(sid);
        buffHexid = new HexId(sid);

    }

    @EventListener
    public void start(StartEvent event) {
        if (gameServerProperties.getServerType() <= 1) {
            SqlDataHelper sqlDataHelper = applicationContextProvider.getBean(SqlDataHelper.class);
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
                    nameRidMap.bind(name, roleId);
                }
            }
        }
    }

    @Override public RoleEntity roleEntity(long rid) {
        return getPlayerStrategyFactory.roleEntity(rid);
    }

    @Override public Collection<Player> cacheAllPlayer() {
        return getPlayerStrategyFactory.cacheAllPlayer();
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

    public void kickAccountAll() {
        for (Map.Entry<String, UserMapping> entry : userMappingMap.entrySet()) {
            kickAccount(entry.getKey());
        }
    }

    public void kickAccount(String account) {
        UserMapping userMapping = getUserMapping(account);
        if (userMapping == null) {
            return;
        }
        if (userMapping.getSocketSession() != null) {
            ResKick resKick = new ResKick();
            resKick.setReason("被运营后台强制下线");
            ChannelFuture channelFuture = userMapping.writeAndFlush(resKick);
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("强制下线失败：{}", account, future.cause());
                }
                userMapping.getSocketSession().close("被运营后台强制下线");
            });
        }
    }

    public UserMapping getUserMapping(String account) {
        return userMappingMap.computeIfAbsent(account, k -> new UserMapping(account, this));
    }

}
