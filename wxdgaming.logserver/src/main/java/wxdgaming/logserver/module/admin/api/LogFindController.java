package wxdgaming.logserver.module.admin.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.boot2.starter.excel.WriteExcel;
import wxdgaming.logserver.bean.LogField;
import wxdgaming.logserver.bean.LogMappingInfo;
import wxdgaming.logserver.module.data.DataCenterService;
import wxdgaming.logserver.module.logs.LogService;
import wxdgaming.logserver.plugin.impl.StatsPlugin;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    final DataCenterService dataCenterService;

    public LogFindController(StatsPlugin statsPlugin, LogService logService, DataCenterService dataCenterService) {
        this.statsPlugin = statsPlugin;
        this.logService = logService;
        this.dataCenterService = dataCenterService;
    }

    @RequestMapping("/nav")
    public RunResult nav() {
        List<JSONObject> nav = logService.nav();
        return RunResult.ok().data(nav);
    }

    /** 查询日志表的表头 */
    @RequestMapping("/title")
    public RunResult logTitle(@RequestParam("tableName") String tableName) {
        AssertUtil.isTrue(StringUtils.isNotBlank(tableName), "tableName不能为空");
        return logService.logTitle(tableName);
    }

    /** 实时大屏展示信息 */
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
        jsonObject.put("rechargeOrderAmount", rechargeOrderAmount / 100);
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
        Pair<Long, Map<String, Long>> online = statsPlugin.online(sqlDataHelper);
        jsonObject.put("onlineSize", online.getKey());
        jsonObject.put("onlineHour", online.getValue());
        jsonObject.put("onlineDistribution", statsPlugin.onlineDistribution(sqlDataHelper, String.valueOf(dataKey)));
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

    @RequestMapping("/excel")
    public RunResult outExcel(
            @RequestParam("tableName") String tableName,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("minTime") String minTime,
            @RequestParam("maxTime") String maxTime,
            @RequestParam("where") String where,
            @RequestParam("order") String orderJson
    ) {
        LogMappingInfo logMappingInfo = dataCenterService.getLogMappingInfoMap().get(tableName);
        RunResult dataResult = logService.logPage(tableName, pageIndex, pageSize, minTime, maxTime, where, orderJson);
        List<JSONObject> data = (List<JSONObject>) dataResult.get("data");

        String fileName = tableName + "_" + MyClock.formatDate("yyyy_MM_dd_HH_mm_ss_SSS") + "_" + System.nanoTime() + ".xlsx";

        Path yyyyMmDdHhMmSsSss = Path.of("./target/out/" + fileName);
        WriteExcel writeExcel = new WriteExcel(yyyyMmDdHhMmSsSss);
        writeExcel.createSheet(logMappingInfo.getLogComment());
        JSONObject fieldComment = new JSONObject(true);
        for (LogField logField : logMappingInfo.getFieldList()) {
            writeExcel.addTitle(logField.getFieldName());
            fieldComment.put(logField.getFieldName(), logField.getFieldComment());
        }
        writeExcel.addRow(fieldComment);
        for (JSONObject datum : data) {
            writeExcel.addRow(datum);
        }
        writeExcel.saveFile();
        return RunResult.ok().data("/" + fileName);
    }
}
