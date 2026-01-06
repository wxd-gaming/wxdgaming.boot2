package wxdgaming.logserver.module.logs;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.io.FileWriteUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.NumberUtil;
import wxdgaming.boot2.starter.batis.sql.SqlQueryBuilder;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.logserver.bean.LogEntity;
import wxdgaming.logserver.bean.LogMappingInfo;
import wxdgaming.logserver.bean.LogTableContext;
import wxdgaming.logserver.module.data.DataCenterService;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void submitLog(List<LogEntity> logEntityList) {
        List<LogEntity> list = logEntityList.stream()
                .peek(logEntity -> {
                    String logType = logEntity.getLogType().toLowerCase();
                    LogTableContext logTableContext = logTableContext(logType);
                    if (logEntity.getUid() == 0) {
                        logEntity.setUid(logTableContext.newId());
                    }
                })
                .filter(logEntity -> {
                    String logType = logEntity.getLogType().toLowerCase();
                    LogTableContext logTableContext = logTableContext(logType);
                    if (logTableContext.filter(logEntity.getUid())) {
                        log.debug("uid 已存在丢弃 {}", logEntity);
                        return false;
                    }
                    return true;
                })
                .peek(logEntity -> {
                    String logType = logEntity.getLogType().toLowerCase();
                    LogTableContext logTableContext = logTableContext(logType);
                    logEntity.checkDataKey();
                    logTableContext.addFilter(logEntity.getUid());
                    log.debug("保存 uid={}, logType={}, entity={}", logEntity.getUid(), logType, logEntity);
                }).toList();

        pgsqlDataHelper.insertList(list, entity -> {
            LogEntity logEntity = (LogEntity) entity;
            String logType = logEntity.getLogType();
            long uid = logEntity.getUid();
            FileWriteUtil.writeString(
                    Paths.get("slog", "error", "insert", MyClock.formatDate(MyClock.SDF_YYYYMMDD), logType, uid + ".log").toFile(),
                    logEntity.toJSONString()
            );
        });

    }

    public void updateLog(List<LogEntity> logEntityList) {
        logEntityList.forEach(logEntity -> {
            String logType = logEntity.getLogType().toLowerCase();
            logEntity.setDayKey(0);
            log.debug("保存 uid={}, logType={}, entity={}", logEntity.getUid(), logType, logEntity);
        });
        pgsqlDataHelper.saveList(logEntityList, (type, entity) -> {
            LogEntity logEntity = (LogEntity) entity;
            String logType = logEntity.getLogType();
            long uid = logEntity.getUid();
            FileWriteUtil.writeString(
                    Paths.get("slog", "error", type, MyClock.formatDate(MyClock.SDF_YYYYMMDD), logType, uid + ".log").toFile(),
                    logEntity.toJSONString()
            );
        });
    }

    public void saveErrorLog(String reason, String data, String stackTrace) {
        LogEntity logEntity = new LogEntity();
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType("systemerrorlog");
        logEntity.getLogData()
                .fluentPut("reason", reason)
                .fluentPut("data", data)
                .fluentPut("stackTrace", stackTrace);
        logEntity.checkDataKey();

        String logType = logEntity.getLogType().toLowerCase();
        LogTableContext logTableContext = logTableContext(logType);
        logEntity.setUid(logTableContext.newId());

        log.debug("保存 uid={}, logType={}, entity={}", logEntity.getUid(), logType, logEntity);
        pgsqlDataHelper.getDataBatch().insert(logEntity);
    }

    public List<JSONObject> nav() {
        List<JSONObject> collect = dataCenterService.getLogMappingInfoList().stream()
                .map(li -> {
                    JSONObject jsonObject = MapOf.newJSONObject();
                    jsonObject.put("name", li.getLogName());
                    jsonObject.put("comment", li.getLogComment());
                    jsonObject.put("routing", li.getRouting());
                    return jsonObject;
                })
                .collect(Collectors.toList());
        collect.addFirst(MapOf.newJSONObject().fluentPut("name", "real").fluentPut("comment", "实时大屏").fluentPut("routing", "/game-real.html"));
        return collect;
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
                    String fieldHtmlTips = logField.getFieldHtmlTips();
                    if (StringUtils.isBlank(fieldHtmlTips)) {
                        fieldHtmlTips = "";
                    }
                    jsonObject.put("title", fieldHtmlTips);
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
                             String minTime, String maxTime, String whereJson, String orderJson) {
        SqlQueryBuilder queryBuilder = pgsqlDataHelper.queryBuilder();
        queryBuilder.setTableName(tableName);

        if (StringUtils.isNotBlank(minTime)) {
            queryBuilder.pushWhereByValueNotNull("daykey>=?", NumberUtil.retainNumber(minTime));
        }

        if (StringUtils.isNotBlank(maxTime)) {
            queryBuilder.pushWhereByValueNotNull("daykey<=?", NumberUtil.retainNumber(maxTime));
        }

        LogMappingInfo logMappingInfo = dataCenterService.getLogMappingInfoMap().get(tableName);
        if (StringUtils.isNotBlank(whereJson)) {
            List<JSONObject> jsonObjects = JSON.parseArray(whereJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String whereFiled = jsonObject.getString("where");
                Function<String, Object> stringObjectFunction = logMappingInfo.fieldValueFunction(whereFiled);
                String and = jsonObject.getString("and");
                String where;
                Object whereValue = stringObjectFunction.apply(jsonObject.getString("whereValue"));
                if ("uid".equals(whereFiled) || "createTime".equals(whereFiled)) {
                    where = whereFiled + " " + and + " ?";
                } else {
                    switch (and) {
                        case "ilike" -> {
                            where = "logdata::jsonb->>'%s' ILIKE ?".formatted(whereFiled);
                            whereValue = "%" + whereValue + "%";
                        }
                        case "notilike" -> {
                            where = "logdata::jsonb->>'%s' NOT ILIKE ?".formatted(whereFiled);
                            whereValue = "%" + whereValue + "%";
                        }
                        case "<=", "<", ">=", ">" ->
                                where = "CAST(logdata::jsonb->>'%s' AS numeric) %s ?".formatted(whereFiled, and);
                        case null, default ->
                                where = "logdata::jsonb @> jsonb_build_object('%s',?)".formatted(whereFiled);
                    }
                }
                queryBuilder.pushWhereAnd(where, whereValue);
            }
        }

        queryBuilder.page(pageIndex, pageSize, 1, 1000);

        if (StringUtils.isNotBlank(orderJson)) {
            StringBuilder stringBuilder = new StringBuilder();
            List<JSONObject> jsonObjects = JSON.parseArray(orderJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String orderField = jsonObject.getString("orderField");
                String orderOption = jsonObject.getString("orderOption");
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(",");
                }
                if ("uid".equals(orderField) || "createTime".equals(orderField)) {
                    stringBuilder.append(orderField);
                } else {
                    stringBuilder.append("logdata::jsonb->>'%s'".formatted(orderField));
                }
                stringBuilder.append(" ").append(orderOption);
            }
            queryBuilder.setOrderBy(stringBuilder.toString());
        } else {
            queryBuilder.setOrderBy("createtime desc");
        }

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
