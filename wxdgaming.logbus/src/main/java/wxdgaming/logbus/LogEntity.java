package wxdgaming.logbus;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.MapOf;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:43
 **/
@Getter
@Setter
public class LogEntity extends ObjectBase {

    /** 归属于那一天 因为联合主键，索引是最左原则 */
    @JSONField(ordinal = 1)
    private int dayKey;
    @JSONField(ordinal = 2)
    private long uid = 0;
    /** 具体的日期 */
    @JSONField(ordinal = 3)
    private long createTime;
    @JSONField(ordinal = 4)
    private String logType;
    @JSONField(ordinal = 99)
    private final JSONObject logData = MapOf.newJSONObject();

    public LogEntity putLogData(String key, Object value) {
        logData.put(key, value);
        return this;
    }

}
