package wxdgaming.boot2.core.threading;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 线程池配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 15:05
 **/
@Getter
public class ExecutorConfig extends ObjectBase {

    public static final ExecutorConfig DEFAULT_INSTANCE = new ExecutorConfig(2, 4, 5000);
    public static final ExecutorConfig LOGIC_INSTANCE = new ExecutorConfig(2, 4, 5000);
    public static final ExecutorConfig VIRTUAL_INSTANCE = new ExecutorConfig(100, 200, 5000);

    @JSONField(ordinal = 1)
    private final int coreSize;
    @JSONField(ordinal = 2)
    private final int maxSize;
    @JSONField(ordinal = 3)
    private final int maxQueueSize;

    @JSONCreator
    public ExecutorConfig(
            @JSONField(name = "coreSize", defaultValue = "2") int coreSize,
            @JSONField(name = "maxSize", defaultValue = "4") int maxSize,
            @JSONField(name = "maxQueueSize", defaultValue = "5000") int maxQueueSize) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.maxQueueSize = maxQueueSize;
    }
}
