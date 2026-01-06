package wxdgaming.logserver.bean;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.Entity;
import wxdgaming.boot2.starter.batis.EntityName;
import wxdgaming.boot2.starter.batis.ann.Convert;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;
import wxdgaming.boot2.starter.batis.sql.ann.Partition;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:43
 **/
@Getter
@Setter
@DbTable
public class LogEntity extends Entity implements Serializable, EntityName {

    @Serial private static final long serialVersionUID = 1L;
    /** 归属于那一天 因为联合主键，索引是最左原则 */
    @Partition
    @JSONField(ordinal = 1)
    @DbColumn(key = true, comment = "分区信息")
    private int dayKey;
    @DbColumn(key = true)
    @JSONField(ordinal = 2)
    private long uid = 0;
    /** 具体的日期 */
    @JSONField(ordinal = 3)
    @DbColumn(index = true)
    private long createTime;
    @JSONField(ordinal = 4)
    @DbColumn(ignore = true)
    private String logType;

    @JSONField(ordinal = 99)
    @DbColumn(columnType = ColumnType.Jsonb, index = true)
    @Convert(JsonbConvert.class)
    private final JSONObject logData = MapOf.newJSONObject();

    @Override public String tableName() {
        return logType.toLowerCase();
    }

    public void checkDataKey() {
        if (getCreateTime() == 0) {
            setCreateTime(System.currentTimeMillis());
        }

        LocalDate localDate = MyClock.localDate(getCreateTime());
        setDayKey(localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth());
    }

    @Override public final boolean equals(Object o) {
        if (!(o instanceof LogEntity logEntity)) return false;

        return getDayKey() == logEntity.getDayKey() && getUid() == logEntity.getUid();
    }

    @Override public int hashCode() {
        int result = getDayKey();
        result = 31 * result + Long.hashCode(getUid());
        return result;
    }

    public LogEntity putLogData(String key, Object value) {
        logData.put(key, value);
        return this;
    }

}
