package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.TableMapping;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.login.entity.UserData;
import wxdgaming.game.login.login.LoginService;
import wxdgaming.game.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色账号
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-13 20:37
 **/
@Slf4j
@RestController
@RequestMapping("/admin/userData")
public class UserDataController implements InitPrint {

    final SqlDataHelper sqlDataHelper;
    final LoginService loginService;

    public UserDataController(PgsqlDataHelper sqlDataHelper, LoginService loginService) {
        this.sqlDataHelper = sqlDataHelper;
        this.loginService = loginService;
    }

    @RequestMapping("/banLogin")
    public RunResult banLogin(@RequestParam("account") String account, @RequestParam("banTime") String banTime) {
        UserData userData = loginService.userData(account);
        if (userData == null) {
            return RunResult.fail("用户不存在");
        }
        long time = 0;
        if (StringUtils.isNotBlank(banTime)) {
            time = MyClock.parseDate("yyyy-MM-dd'T'HH:mm", banTime).getTime();
        }

        if (time < System.currentTimeMillis())
            time = 0;
        userData.setBanExpireTime(time);
        sqlDataHelper.getCacheService().cache(UserData.class).put(account, userData);
        log.info("管理：{} 设置：{} 禁止登录：{}", AdminUserToken.threadContext().getUserName(), account, banTime);
        return RunResult.ok().msg("设置成功");
    }

    @RequestMapping("/gmLevel")
    public RunResult gmLevel(@RequestParam("account") String account, @RequestParam("gmLevel") int gmLevel) {
        UserData userData = loginService.userData(account);
        if (userData == null) {
            return RunResult.fail("用户不存在");
        }
        if (gmLevel < 0) gmLevel = 0;
        userData.setGmLevel(gmLevel);
        sqlDataHelper.getCacheService().cache(UserData.class).put(account, userData);
        log.info("管理：{} 设置：{} GM等级为：{}", AdminUserToken.threadContext().getUserName(), account, gmLevel);
        return RunResult.ok().msg("设置成功");
    }

    @RequestMapping("/whiteLogin")
    public RunResult whiteLogin(@RequestParam("account") String account) {
        UserData userData = loginService.userData(account);
        if (userData == null) {
            return RunResult.fail("用户不存在");
        }
        userData.setWhite(!userData.isWhite());
        sqlDataHelper.getCacheService().cache(UserData.class).put(account, userData);
        log.info("管理：{} 设置：{} 白名单：{}", AdminUserToken.threadContext().getUserName(), account, userData.isWhite());
        return RunResult.ok().msg(userData.isWhite() ? "设置白名单成功" : "取消白名单成功");
    }

    @RequestMapping("/queryList")
    public RunResult queryList(@RequestParam("pageIndex") int pageIndex,
                               @RequestParam("pageSize") int pageSize,
                               @RequestParam("minTime") String minTime,
                               @RequestParam("maxTime") String maxTime,
                               @RequestParam("where") String whereJson,
                               @RequestParam("order") String orderJson) {
        SqlQueryBuilder queryBuilder = sqlDataHelper.queryBuilder();
        queryBuilder.sqlByEntity(UserData.class);

        TableMapping userDataMapping = sqlDataHelper.tableMapping(UserData.class);

        if (StringUtils.isNotBlank(whereJson)) {
            List<JSONObject> jsonObjects = JSON.parseArray(whereJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String whereFiled = jsonObject.getString("where");
                String and = jsonObject.getString("and");
                String where = whereFiled + " " + and + " ?";
                Object whereValue = jsonObject.getObject("whereValue", userDataMapping.getColumns().get(whereFiled).getFileType());
                queryBuilder.pushWhereAnd(where, whereValue);
            }
        }
        if (StringUtils.isNotBlank(minTime)) {
            Date minDate = MyClock.parseDate("yyyy-MM-dd'T'HH:mm", minTime);
            queryBuilder.pushWhereAnd("createtime >= ?::int8", minDate.getTime());
        }

        if (StringUtils.isNotBlank(maxTime)) {
            Date maxDate = MyClock.parseDate("yyyy-MM-dd'T'HH:mm", maxTime);
            queryBuilder.pushWhereAnd("createtime <= ?::int8", maxDate.getTime());
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
            queryBuilder.setOrderBy("createtime desc");
        }
        queryBuilder.page(pageIndex, pageSize, 1, 1000);
        long rowCount = queryBuilder.findCount();
        List<UserData> list2Entity = queryBuilder.findList2Entity(UserData.class);

        List<JSONObject> list = new ArrayList<>();
        for (UserData userData : list2Entity) {
            JSONObject jsonObject = userData.toJSONObject();
            jsonObject.remove("token");
            jsonObject.put("createTime", Util.formatWebDate(userData.getCreateTime()));
            jsonObject.put("banExpireTime", Util.formatWebDate(userData.getBanExpireTime()));
            jsonObject.put("lastLoginTime", Util.formatWebDate(userData.getLastLoginTime()));
            list.add(jsonObject);
        }
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }

}
