package wxdgaming.logserver.module.logs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.bean.LogMappingInfo;
import wxdgaming.logserver.bean.LogTableContext;
import wxdgaming.logserver.module.data.DataCenterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 日志服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:04
 **/
@Slf4j
@Service
public class LogService implements InitPrint {

    final PgsqlDataHelper pgsqlDataHelper;
    final DataCenterService dataCenterService;
    final Map<String, LogTableContext> logTableContextMap = MapOf.newConcurrentHashMap();

    public LogService(PgsqlDataHelper pgsqlDataHelper, DataCenterService dataCenterService) {
        this.pgsqlDataHelper = pgsqlDataHelper;
        this.dataCenterService = dataCenterService;
    }

    public LogTableContext logTableContext(String logName) {
        return logTableContextMap.computeIfAbsent(logName, LogTableContext::new);
    }

    public void submitLog(LogEntity logEntity) {
        String logType = logEntity.getLogType().toLowerCase();
        LogTableContext logTableContext = logTableContext(logType);
        if (logEntity.getUid() == 0) {
            log.debug("uid 为0 {}", logEntity);
            logEntity.setUid(logTableContext.newId());
        }
        if (logTableContext.filter(logEntity.getUid())) {
            log.debug("uid 已存在丢弃 {}", logEntity);
            return;
        }
        logEntity.checkDataKey();
        logTableContext.addFilter(logEntity.getUid());
        log.debug("保存 uid={}, logType={}, entity={}", logEntity.getUid(), logType, logEntity);
        pgsqlDataHelper.getDataBatch().insert(logEntity);
    }

    public List<JSONObject> nav() {
        return dataCenterService.getLogMappingInfoList().stream()
                .map(li -> {
                    JSONObject jsonObject = MapOf.newJSONObject();
                    jsonObject.put("name", li.getLogName());
                    jsonObject.put("comment", li.getLogComment());
                    jsonObject.put("routing", li.getRouting());
                    return jsonObject;
                })
                .toList();
    }

    public RunResult logTitle(String tableName) {

        LogMappingInfo logMappingInfo = dataCenterService.getLogMappingInfoMap().get(tableName);

        List<JSONObject> list = logMappingInfo.getFieldList().stream()
                .map(logField -> {
                    JSONObject jsonObject = MapOf.newJSONObject();
                    jsonObject.put("name", logField.getFieldName());
                    jsonObject.put("comment", logField.getFieldComment());
                    String fieldHtmlStyle = logField.getFieldHtmlStyle();
                    if (StringUtils.isBlank(fieldHtmlStyle)) {
                        fieldHtmlStyle = "";
                    }
                    jsonObject.put("style", fieldHtmlStyle);
                    return jsonObject;
                })
                .toList();

        String htmlStyle = logMappingInfo.getHtmlStyle();
        if (StringUtils.isBlank(htmlStyle)) {
            htmlStyle = "";
        }
        return RunResult.ok()
                .fluentPut("comment", logMappingInfo.getLogComment())
                .fluentPut("style", htmlStyle)
                .data(list);
    }

    public RunResult logPage(String tableName,
                             int pageIndex, int pageSize,
                             String minDay, String maxDay, String whereJson) {
        SqlQueryBuilder queryBuilder = pgsqlDataHelper.queryBuilder();
        queryBuilder.setTableName(tableName);

        if (StringUtils.isNotBlank(minDay)) {
            queryBuilder.pushWhereByValueNotNull("daykey>=?", NumberUtil.retainNumber(minDay));
        }

        if (StringUtils.isNotBlank(maxDay)) {
            queryBuilder.pushWhereByValueNotNull("daykey<=?", NumberUtil.retainNumber(maxDay));
        }

        LogMappingInfo logMappingInfo = dataCenterService.getLogMappingInfoMap().get(tableName);
        if (StringUtils.isNotBlank(whereJson)) {
            List<JSONObject> jsonObjects = JSON.parseArray(whereJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String where = jsonObject.getString("where");
                Function<String, Object> stringObjectFunction = logMappingInfo.fieldValueFunction(where);
                String and = jsonObject.getString("and");
                String format;
                if ("uid".equals(where) || "createTime".equals(where)) {
                    format = "%s " + and + " ?";
                } else if ("<=".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) <= ?";
                } else if ("<".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) < ?";
                } else if (">=".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) >= ?";
                } else if (">".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) > ?";
                } else {
                    format = "logdata::jsonb @> jsonb_build_object('%s',?)";
                }
                Object whereValue = stringObjectFunction.apply(jsonObject.getString("whereValue"));
                queryBuilder.pushWhereAnd(format.formatted(where), whereValue);
            }
        }

        queryBuilder.page(pageIndex, pageSize, 1, 1000);
        queryBuilder.setOrderBy("createtime desc");
        System.out.println(queryBuilder.buildSelectSql());
        long rowCount = queryBuilder.findCount();
        List<LogEntity> logEntities = queryBuilder.findList2Entity(LogEntity.class);
        List<JSONObject> list = new ArrayList<>();
        for (LogEntity logEntity : logEntities) {
            JSONObject jsonObject = new JSONObject(logEntity.getLogData());
            jsonObject.put("uid", logEntity.getUid());
            jsonObject.put("dataKey", logEntity.getDayKey());
            jsonObject.put("createTime", MyClock.formatDate("yyyy-MM-dd HH:mm:ss.SSS", logEntity.getCreateTime()));
            list.add(jsonObject);
        }
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }

}
