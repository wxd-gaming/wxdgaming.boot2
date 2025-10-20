package wxdgaming.logserver.plugin.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.ApplicationContextProvider;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.plugin.AbstractPlugin;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * 统计测试 stats
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 18:28
 **/
@Slf4j
@Component
public class StatsPlugin extends AbstractPlugin {

    @Override public String cron() {
        return "0 * * * * ?";
    }

    @Override public void trigger(ApplicationContextProvider runApplication) {
        SqlDataHelper sqlDataHelper = runApplication.getBean(SqlDataHelper.class);
        LocalDate now = LocalDate.now();
        int beforeDay = 121;
        LocalDate startDate = now.plusDays(-beforeDay);
        for (int i = 0; i <= beforeDay; i++) {
            LocalDate localDate = startDate.plusDays(i);
            log.info("开始处理{}", localDate);
            try {
                int dataKey = localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth();
                int registerRoleCount = registerRoleCount(sqlDataHelper, dataKey);
                int registerAccountCount = registerAccountCount(sqlDataHelper, dataKey);
                int activeAccountCount = activeAccountCount(sqlDataHelper, dataKey);
                JSONObject jsonObject = new JSONObject(true);
                jsonObject.put("registerAccountCount", registerAccountCount);
                jsonObject.put("registerRoleCount", registerRoleCount);
                jsonObject.put("activeAccountCount", activeAccountCount);
                int rechargeOrderCount = rechargeOrderCount(sqlDataHelper, dataKey);
                int rechargeAccountCount = rechargeAccountCount(sqlDataHelper, dataKey);
                long rechargeOrderAmount = rechargeOrderAmount(sqlDataHelper, dataKey);
                long registerAccountRechargeCount = registerAccountRechargeCount(sqlDataHelper, dataKey);
                jsonObject.put("rechargeOrderCount", rechargeOrderCount);
                jsonObject.put("rechargeAccountCount", rechargeAccountCount);
                jsonObject.put("rechargeOrderAmount", rechargeOrderAmount);
                jsonObject.put("registerAccountRechargeCount", registerAccountRechargeCount);
                /*ARPA = 收入/活跃账号数*/
                jsonObject.put("ARPU", activeAccountCount > 0 ? rechargeOrderAmount / activeAccountCount / 100f : 0.0f);
                /*CARPUS = 收入/付费账号数*/
                jsonObject.put("ARPPU", rechargeAccountCount > 0 ? rechargeOrderAmount / rechargeAccountCount / 100f : 0.0f);
                if (registerAccountCount > 0) {
                    for (int j = 0; j <= beforeDay; j++) {
                        LocalDate loginDate = localDate.plusDays(j);
                        if (loginDate.isAfter(now))
                            break;
                        int loginDataKey = loginDate.getYear() * 10000 + loginDate.getMonthValue() * 100 + loginDate.getDayOfMonth();
                        int loginAccountCount = loginAccountCount(sqlDataHelper, dataKey, loginDataKey);
                        if (loginAccountCount < 1) continue;
                        float v = loginAccountCount * 10000 / registerAccountCount / 100f;
                        jsonObject.put("login_" + (j + 1), String.format("%.2f", v) + "%");
                    }
                }
                log.info("{} {} {}", localDate, dataKey, jsonObject);
                addLog(sqlDataHelper, "stats", MyClock.time2Milli(localDate), jsonObject);
            } catch (Exception e) {
                log.error("处理异常 {}", localDate, e);
            }
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

    /** 查询今日充值的订单数 */
    public int rechargeOrderCount(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = "SELECT count(1) FROM (SELECT logdata->>'spOrderId' as spOrderId FROM rolerechargeslog WHERE daykey= ? GROUP BY logdata->>'spOrderId') as rc;";
        return sqlDataHelper.executeScalar(sql, Integer.class, loginDataKey);
    }

    /** 查询今日充值的账号数 */
    public int rechargeAccountCount(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = "SELECT count(1) FROM (SELECT logdata->>'account' as account FROM rolerechargeslog WHERE daykey= ? GROUP BY logdata->>'account') as rc;";
        return sqlDataHelper.executeScalar(sql, Integer.class, loginDataKey);
    }

    public long rechargeOrderAmount(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = "SELECT sum(amount) FROM (SELECT sum((logdata->>'amount')::int8) as amount FROM rolerechargeslog WHERE daykey= ? GROUP BY logdata->>'spOrderId') as rc;";
        Long aLong = sqlDataHelper.executeScalar(sql, Long.class, loginDataKey);
        return aLong == null ? 0 : aLong;
    }

    /** 新增注册账号充值账号数 */
    public long registerAccountRechargeCount(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = """
                SELECT count(1) FROM (SELECT logdata->>'account' as account FROM rolerechargeslog WHERE daykey= ? GROUP BY logdata->>'account') as ra
                WHERE ra.account in((SELECT logdata->>'account' as account FROM accountregisterlog WHERE daykey= ? GROUP BY logdata->>'account'));
                """;
        Long aLong = sqlDataHelper.executeScalar(sql, Long.class, loginDataKey, loginDataKey);
        return aLong == null ? 0 : aLong;
    }

    /** 充值金额分组统计 */
    public int[][] rechargeGroup(SqlDataHelper sqlDataHelper, int loginDataKey) {
        String sql = """
                SELECT ((logdata->>'amount')::int) as amount,count(1) as count FROM rolerechargeslog\s
                WHERE daykey= ?\s
                and uid in(SELECT min(uid) as uid FROM rolerechargeslog WHERE daykey= ? GROUP BY logdata->>'spOrderId')
                GROUP BY logdata->>'amount';
                """;
        List<JSONObject> jsonObjects = sqlDataHelper.queryList(sql, loginDataKey, loginDataKey);
        jsonObjects.sort(Comparator.comparingInt(o -> o.getIntValue("amount")));
        int[][] ints = new int[2][jsonObjects.size()];
        for (int i = 0; i < jsonObjects.size(); i++) {
            JSONObject jsonObject = jsonObjects.get(i);
            ints[0][i] = jsonObject.getInteger("amount") / 100;
            ints[1][i] = jsonObject.getInteger("count");
        }
        return ints;
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
