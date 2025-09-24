package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.function.FunctionUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.global.GlobalDataConst;
import wxdgaming.game.login.bean.global.ServerShowName;
import wxdgaming.game.login.bean.global.ServerShowNameGlobalData;
import wxdgaming.game.login.entity.ServerInfoEntity;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对外接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 09:34
 **/
@Slf4j
@RestController
@RequestMapping("/admin/gameServer")
public class AdminGameServerController implements InitPrint {

    final LoginServerProperties loginServerProperties;
    final SqlDataHelper sqlDataHelper;
    final InnerService innerService;
    final GlobalDataService globalDataService;

    public AdminGameServerController(LoginServerProperties loginServerProperties, PgsqlDataHelper sqlDataHelper,
                                     InnerService innerService, GlobalDataService globalDataService) {
        this.loginServerProperties = loginServerProperties;
        this.sqlDataHelper = sqlDataHelper;
        this.innerService = innerService;
        this.globalDataService = globalDataService;
    }

    @RequestMapping(value = "/addServer")
    public RunResult addServer(HttpServletRequest context,
                               @RequestParam("serverId") int serverId,
                               @RequestParam("serverName") String serverName,
                               @RequestParam("openTime") String openTime) {
        AssertUtil.assertTrue(!innerService.getInnerGameServerInfoMap().containsKey(serverId), "服务器id重复");
        ServerInfoEntity entity = new ServerInfoEntity();
        entity.setServerId(serverId);
        entity.setName(serverName);
        entity.setOpenTime(Util.parseWebDate(openTime));
        innerService.getInnerGameServerInfoMap().put(serverId, entity);
        sqlDataHelper.insert(entity);
        return RunResult.ok().msg("添加成功");
    }

    @RequestMapping(value = "/editServer")
    public RunResult editServer(HttpServletRequest context,
                                @RequestParam("serverId") int serverId,
                                @RequestParam("serverName") String serverName,
                                @RequestParam("openTime") String openTime,
                                @RequestParam(value = "showLevel") int showLevel) {
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        long time = Util.parseWebDate(openTime);
        if (time < System.currentTimeMillis() && time < entity.getOpenTime()) {
            if (!loginServerProperties.isDebug()) {
                return RunResult.fail("正式环境已经开服不允许修改服务器时间");
            }
        }
        entity.setName(serverName);
        entity.setOpenTime(time);
        entity.setShowLevel(showLevel);
        sqlDataHelper.update(entity);
        return RunResult.ok().msg("修改成功");
    }

    @RequestMapping(value = "/editServerOpenTime")
    public RunResult editServerOpenTime(@RequestParam("serverId") int serverId,
                                        @RequestParam("openTime") String openTime) {
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        long time = Util.parseWebDate(openTime);
        if (time < System.currentTimeMillis() && time < entity.getOpenTime()) {
            if (!loginServerProperties.isDebug()) {
                return RunResult.fail("正式环境已经开服不允许修改服务器时间");
            }
        }
        entity.setOpenTime(time);
        sqlDataHelper.update(entity);
        return RunResult.ok().msg("修改成功");
    }

    @RequestMapping(value = "/editServerMaintenanceTime")
    public RunResult editServerMaintenanceTime(@RequestParam("serverId") int serverId,
                                               @RequestParam("maintenanceTime") String maintenanceTime) {
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        long time = Util.parseWebDate(maintenanceTime);
        if (time < System.currentTimeMillis()) {
            return RunResult.fail("维护时间小于当前时间");
        }
        entity.setMaintenanceTime(time);
        sqlDataHelper.update(entity);
        return RunResult.ok().msg("修改成功");
    }

    @RequestMapping(value = "/editShowName")
    public RunResult editShowName(@RequestParam("serverId") int serverId,
                                  @RequestParam("showName") String showName,
                                  @RequestParam("showNameExpireTime") String showNameExpireTime) {
        ServerShowNameGlobalData showNameGlobalData = globalDataService.get(GlobalDataConst.ServerNameGlobalData);
        ConcurrentHashMap<Integer, ServerShowName> serverNameMap = showNameGlobalData.getServerNameMap();
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        long time = Util.parseWebDate(showNameExpireTime);
        if (time < System.currentTimeMillis()) {
            serverNameMap.remove(serverId);
            return RunResult.ok().msg("删除成功");
        } else {
            serverNameMap.put(serverId, new ServerShowName().setName(showName).setExpireTime(time));
            return RunResult.ok().msg("设置成功");
        }
    }

    /** 踢玩家下线 */
    @RequestMapping(value = "/kick")
    public RunResult kick(@RequestParam("serverId") int serverId) {
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("account", "ALL");
        innerService.executeServer("kick", "yunying/kick", params, entity);
        return RunResult.ok().msg("执行成功");
    }

    @RequestMapping(value = "/editServerShowLevel")
    public RunResult editServerShowLevel(@RequestParam("serverId") int serverId,
                                         @RequestParam("showLevel") int showLevel) {
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(serverId);
        AssertUtil.assertTrue(entity != null, "服务器不存在");
        entity.setShowLevel(showLevel);
        sqlDataHelper.update(entity);
        return RunResult.ok().msg("修改成功");
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryGameServerList(HttpServletRequest context,
                                         @RequestParam("pageIndex") int pageIndex,
                                         @RequestParam("pageSize") int pageSize,
                                         @RequestParam("minTime") String minTime,
                                         @RequestParam("maxTime") String maxTime,
                                         @RequestParam("where") String whereJson,
                                         @RequestParam("order") String orderJson) {

        SqlQueryBuilder queryBuilder = sqlDataHelper.queryBuilder();
        queryBuilder.sqlByEntity(ServerInfoEntity.class);


        if (StringUtils.isNotBlank(whereJson)) {
            List<JSONObject> jsonObjects = JSON.parseArray(whereJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String whereFiled = jsonObject.getString("where");
                String and = jsonObject.getString("and");
                String where = whereFiled + " " + and + " ?";
                queryBuilder.pushWhereAnd(where, jsonObject.getString("whereValue"));
            }
        }
        if (StringUtils.isNotBlank(minTime)) {
            Date minDate = MyClock.parseDate("yyyy-MM-dd'T'HH:mm", minTime);
            queryBuilder.pushWhereAnd("openTime >= ?::int8", minDate.getTime());
        }

        if (StringUtils.isNotBlank(maxTime)) {
            Date maxDate = MyClock.parseDate("yyyy-MM-dd'T'HH:mm", maxTime);
            queryBuilder.pushWhereAnd("openTime <= ?::int8", maxDate.getTime());
        }

        if (StringUtils.isNotBlank(orderJson)) {
            StringBuilder stringBuilder = new StringBuilder();
            List<JSONObject> jsonObjects = JSON.parseArray(orderJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String orderField = jsonObject.getString("orderField");
                String orderOption = jsonObject.getString("orderOption");
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(orderField);
                stringBuilder.append(" ").append(orderOption);
            }
            queryBuilder.setOrderBy(stringBuilder.toString());
        } else {
            queryBuilder.setOrderBy("serverid desc");
        }
        queryBuilder.page(pageIndex, pageSize, 1, 1000);
        long rowCount = queryBuilder.findCount();
        List<ServerInfoEntity> list2Entity = queryBuilder.findList2Entity(ServerInfoEntity.class);

        ServerShowNameGlobalData showNameGlobalData = globalDataService.get(GlobalDataConst.ServerNameGlobalData);
        ConcurrentHashMap<Integer, ServerShowName> serverNameMap = showNameGlobalData.getServerNameMap();

        List<JSONObject> list = new ArrayList<>();
        for (ServerInfoEntity entity : list2Entity) {
            JSONObject jsonObject = entity.toJSONObject();
            jsonObject.remove("token");
            ServerShowName serverShowName = serverNameMap.get(entity.getServerId());
            jsonObject.put("showName", FunctionUtil.nullDefaultValue(serverShowName, ServerShowName::getName, ""));
            jsonObject.put("showNameExpireTime", FunctionUtil.nullDefaultValue(serverShowName, v -> Util.formatWebDate(v.getExpireTime()), ""));
            jsonObject.put("openTime", Util.formatWebDate(entity.getOpenTime()));
            jsonObject.put("maintenanceTime", Util.formatWebDate(entity.getMaintenanceTime()));
            jsonObject.put("lastSyncTime", Util.formatWebDate(entity.getLastSyncTime()));
            list.add(jsonObject);
        }
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }

}
