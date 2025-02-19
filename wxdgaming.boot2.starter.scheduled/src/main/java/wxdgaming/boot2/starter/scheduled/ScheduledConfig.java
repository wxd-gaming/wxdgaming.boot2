package wxdgaming.boot2.starter.scheduled;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 定时器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:31
 **/
@Getter
public class ScheduledConfig extends ObjectBase {

    public static final ScheduledConfig INSTANCE = new ScheduledConfig(1);

    @JSONField(ordinal = 1)
    protected final int coreSize;

    @JSONCreator
    public ScheduledConfig(@JSONField(name = "coreSize") int coreSize) {
        this.coreSize = coreSize;
    }
}
