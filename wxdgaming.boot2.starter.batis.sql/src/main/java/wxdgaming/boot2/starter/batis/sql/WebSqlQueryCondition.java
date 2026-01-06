package wxdgaming.boot2.starter.batis.sql;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.TableMapping;

import java.util.Date;
import java.util.List;

/**
 * 查询条件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-24 20:24
 **/
@Getter
@Setter
public class WebSqlQueryCondition extends ObjectBase {

    int pageIndex;
    int pageSize;
    String minTime;
    String maxTime;
    String whereJson;
    String orderJson;

    String defaultOrderBy = "";

    public SqlQueryBuilder build(SqlDataHelper sqlDataHelper, Class<? extends Entity> entityClass, String timeField) {
        SqlQueryBuilder queryBuilder = sqlDataHelper.queryBuilder();
        queryBuilder.sqlByEntity(entityClass);
        build(queryBuilder, sqlDataHelper.tableMapping(entityClass), timeField);
        return queryBuilder;
    }

    public void build(SqlQueryBuilder queryBuilder, TableMapping tableMapping, String timeField) {
        pushWhereByJson(queryBuilder, whereJson, tableMapping);
        if (StringUtils.isNotBlank(timeField)) {
            pushWhereByWebTime(queryBuilder, timeField, ">=", minTime);
            pushWhereByWebTime(queryBuilder, timeField, "<=", maxTime);
        }
        orderByJson(queryBuilder, orderJson, defaultOrderBy);
        queryBuilder.page(pageIndex, pageSize, 1, 1000);
    }

    /**
     * @param field     字段
     * @param connector 连接符 {@code =, <=, >=}
     * @param webTime   时间格式字符串 yyyy-MM-dd'T'HH:mm
     */
    private void pushWhereByWebTime(SqlQueryBuilder queryBuilder, String field, String connector, String webTime) {
        if (StringUtils.isNotBlank(webTime)) {
            Date minDate = MyClock.parseDate(MyClock.SDF_YYYYMMDDHHMM_10, webTime);
            queryBuilder.pushWhereAnd(field + " " + connector + " ?", minDate.getTime());
        }
    }

    private void orderByJson(SqlQueryBuilder queryBuilder, String json, String defaultOrderBy) {
        if (StringUtils.isNotBlank(json)) {
            StringBuilder stringBuilder = new StringBuilder();
            List<JSONObject> jsonObjects = JSON.parseArray(json, JSONObject.class);
            for (JSONObject sqlQueryOrder : jsonObjects) {
                String orderField = sqlQueryOrder.getString("orderField");
                String orderOption = sqlQueryOrder.getString("orderOption");
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(orderField).append(" ").append(orderOption);
            }
            queryBuilder.setOrderBy(stringBuilder.toString());
        } else if (StringUtils.isNotBlank(defaultOrderBy)) {
            queryBuilder.setOrderBy(defaultOrderBy);
        }
    }

    private void pushWhereByJson(SqlQueryBuilder queryBuilder, String json, TableMapping tableMapping) {
        if (StringUtils.isNotBlank(json)) {
            List<JSONObject> jsonObjects = JSON.parseArray(json, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String whereFiled = jsonObject.getString("where");
                String and = jsonObject.getString("and");
                String where = whereFiled + " " + and + " ?";
                TableMapping.FieldMapping fieldMapping = tableMapping.getColumns().get(whereFiled);
                Object whereValue = jsonObject.getObject("whereValue", fieldMapping.getFileType());
                queryBuilder.pushWhereAnd(where, whereValue);
            }
        }
    }

}
