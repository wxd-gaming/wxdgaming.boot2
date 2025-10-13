package wxdgaming.logserver.module.admin.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.logserver.module.logs.LogService;
import wxdgaming.logserver.plugin.impl.StatsPlugin;

import java.time.LocalDate;
import java.util.List;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:06
 **/
@Slf4j
@RestController
@RequestMapping("/admin/log")
public class LogFindController extends HoldApplicationContext implements InitPrint {

    final StatsPlugin statsPlugin;
    final LogService logService;

    public LogFindController(StatsPlugin statsPlugin, LogService logService) {
        this.statsPlugin = statsPlugin;
        this.logService = logService;
    }

    @RequestMapping("/nav")
    public RunResult nav() {
        List<JSONObject> nav = logService.nav();
        return RunResult.ok().data(nav);
    }

    @RequestMapping("/title")
    public RunResult logTitle(@RequestParam("tableName") String tableName) {
        AssertUtil.isTrue(StringUtils.isNotBlank(tableName), "tableName不能为空");
        return logService.logTitle(tableName);
    }

    @RequestMapping("/real")
    public RunResult real() {
        SqlDataHelper sqlDataHelper = getApplicationContextProvider().getBean(PgsqlDataHelper.class);
        LocalDate localDate = LocalDate.now();
        int dataKey = localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
        int registerRoleCount = statsPlugin.registerRoleCount(sqlDataHelper, dataKey);
        int registerAccountCount = statsPlugin.registerAccountCount(sqlDataHelper, dataKey);
        int activeAccountCount = statsPlugin.activeAccountCount(sqlDataHelper, dataKey);
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.put("registerAccountCount", registerAccountCount);
        jsonObject.put("registerRoleCount", registerRoleCount);
        jsonObject.put("activeAccountCount", activeAccountCount);
        int rechargeOrderCount = statsPlugin.rechargeOrderCount(sqlDataHelper, dataKey);
        int rechargeAccountCount = statsPlugin.rechargeAccountCount(sqlDataHelper, dataKey);
        long rechargeOrderAmount = statsPlugin.rechargeOrderAmount(sqlDataHelper, dataKey);
        long registerAccountRechargeCount = statsPlugin.registerAccountRechargeCount(sqlDataHelper, dataKey);
        jsonObject.put("rechargeOrderCount", rechargeOrderCount);
        jsonObject.put("rechargeAccountCount", rechargeAccountCount);
        jsonObject.put("rechargeOrderAmount", rechargeOrderAmount);
        jsonObject.put("registerAccountRechargeCount", registerAccountRechargeCount);
        /*ARPA = 收入/活跃账号数*/
        jsonObject.put("ARPU", activeAccountCount > 0 ? rechargeOrderAmount / activeAccountCount / 100f : 0.0f);
        /*CARPUS = 收入/付费账号数*/
        jsonObject.put("ARPPU", rechargeAccountCount > 0 ? rechargeOrderAmount / rechargeAccountCount / 100f : 0.0f);
        /*付费率 = 付费账号数/注册账号数*/
        jsonObject.put("fufeilv", registerAccountCount > 0 ? registerAccountRechargeCount * 100f / registerAccountCount : 0.0f);
        /*充值金额分组统计*/
        int[][] rechargeGroup = statsPlugin.rechargeGroup(sqlDataHelper, dataKey);
        jsonObject.put("rechargeGroup", rechargeGroup);
        return RunResult.ok().fluentPutAll(jsonObject);
    }

    @RequestMapping("/page")
    public RunResult logPage(
            @RequestParam("tableName") String tableName,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("minTime") String minTime,
            @RequestParam("maxTime") String maxTime,
            @RequestParam("where") String where,
            @RequestParam("order") String orderJson
    ) {
        return logService.logPage(tableName, pageIndex, pageSize, minTime, maxTime, where, orderJson);
    }

}
