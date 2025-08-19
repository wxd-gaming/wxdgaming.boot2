package wxdgaming.logserver.plugin.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.plugin.AbstractPlugin;

import java.time.LocalDate;

/**
 * 统计测试 stats
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 18:28
 **/
@Slf4j
@Singleton
public class StatsPlugin extends AbstractPlugin {

    @Override public String cron() {
        return "0 * * * * ?";
    }

    @Override public void trigger(RunApplication runApplication) {
        SqlDataHelper sqlDataHelper = runApplication.getInstance(SqlDataHelper.class);
        LocalDate now = LocalDate.now();
        int beforeDay = 121;
        LocalDate startDate = now.plusDays(-beforeDay);
        for (int i = 0; i <= beforeDay; i++) {
            LocalDate localDate = startDate.plusDays(i);
            log.info("开始处理{}", localDate);
            int dataKey = localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
            int registerRoleCount = registerRoleCount(sqlDataHelper, dataKey);
            int registerAccountCount = registerAccountCount(sqlDataHelper, dataKey);
            int activeAccountCount = activeAccountCount(sqlDataHelper, dataKey);
            JSONObject jsonObject = new JSONObject(true);
            jsonObject.put("registerAccountCount", registerAccountCount);
            jsonObject.put("registerRoleCount", registerRoleCount);
            jsonObject.put("activeAccountCount", activeAccountCount);
            jsonObject.put("orderCount", 0);
            jsonObject.put("orderMoneyCount", 0);
            JSONObject loginAccountDay = MapOf.newJSONObject();
            {
                for (int j = 0; j <= beforeDay; j++) {
                    LocalDate loginDate = localDate.plusDays(j);
                    if (loginDate.isAfter(now))
                        break;
                    int loginDataKey = loginDate.getYear() * 10000 + loginDate.getMonthValue() * 100 + loginDate.getDayOfMonth();
                    int loginAccountCount = loginAccountCount(sqlDataHelper, dataKey, loginDataKey);
                    loginAccountDay.put(String.valueOf(j + 1), loginAccountCount);
                }
            }
            jsonObject.put("loginAccountDay", loginAccountDay);
            log.info("{} {} {}", localDate, dataKey, jsonObject);
            addLog(sqlDataHelper, "stats", MyClock.time2Milli(localDate), jsonObject);
        }

    }

    /** 查询指定日期的注册账号数 */
    public int registerAccountCount(SqlDataHelper sqlDataHelper, int dataKey) {
        String sql = "SELECT count(1) FROM (SELECT logdata->>'account' as account FROM accountregisterlog where daykey= ? GROUP BY logdata->>'account') as rc;";
        return sqlDataHelper.executeScalar(sql, Integer.class, dataKey);
    }

    /** 查询指定日期注册账号，指定日期登录数据，留存统计使用 */
    public int loginAccountCount(SqlDataHelper sqlDataHelper, int registerDataKey, int loginDataKey) {
        String sql = """
                SELECT
                    COUNT(*)
                FROM
                	(
                	SELECT
                		logdata ->> 'account' AS account
                	FROM
                		accountloginlog
                	WHERE
                		daykey = ?
                		AND logdata ->> 'account' IN ( SELECT logdata ->> 'account' AS account FROM accountregisterlog WHERE daykey = ? GROUP BY logdata ->> 'account' )
                	GROUP BY
                		logdata ->> 'account'
                	) AS r2;
                """;
        return sqlDataHelper.executeScalar(sql, Integer.class, loginDataKey, registerDataKey);
    }

    /** 查询指定日期注册角色数量 */
    public int registerRoleCount(SqlDataHelper sqlDataHelper, int dataKey) {
        String sql = "SELECT count(1) FROM (SELECT logdata->>'roleId' as roleId FROM roleregisterlog where daykey= ? GROUP BY logdata->>'roleId') as rc;";
        return sqlDataHelper.executeScalar(sql, Integer.class, dataKey);
    }

    /** 查询活跃账号 */
    public int activeAccountCount(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = "SELECT count(1) FROM (SELECT logdata->>'account' as account FROM accountloginlog WHERE daykey= ? GROUP BY logdata->>'account') as rc;";
        return sqlDataHelper.executeScalar(sql, Integer.class, loginDataKey);
    }

    public void addLog(SqlDataHelper sqlDataHelper, String logName, long time, JSONObject jsonObject) {
        LogEntity logEntity = new LogEntity();
        logEntity.setCreateTime(time);
        logEntity.checkDataKey();
        logEntity.setUid(logEntity.getDayKey());
        logEntity.setLogType(logName);
        logEntity.getLogData().putAll(jsonObject);
        sqlDataHelper.getDataBatch().save(logEntity);
    }

}
