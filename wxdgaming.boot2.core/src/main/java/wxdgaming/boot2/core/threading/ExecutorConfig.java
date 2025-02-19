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

    public static final ExecutorConfig INSTANCE = new ExecutorConfig(
            2, 4,
            10, 32,
            100, 200
    );
    @JSONField(ordinal = 1)
    private final int defaultCoreSize;
    @JSONField(ordinal = 2)
    private final int defaultMaxSize;

    @JSONField(ordinal = 10)
    private final int logicCoreSize;
    @JSONField(ordinal = 12)
    private final int logicMaxSize;

    @JSONField(ordinal = 20)
    private final int virtualCoreSize;
    @JSONField(ordinal = 21)
    private final int virtualMaxSize;

    @JSONCreator
    public ExecutorConfig(
            @JSONField(name = "defaultCoreSize") int defaultCoreSize,
            @JSONField(name = "defaultMaxSize") int defaultMaxSize,
            @JSONField(name = "logicCoreSize") int logicCoreSize,
            @JSONField(name = "logicMaxSize") int logicMaxSize,
            @JSONField(name = "virtualCoreSize") int virtualCoreSize,
            @JSONField(name = "virtualMaxSize") int virtualMaxSize) {
        this.defaultCoreSize = defaultCoreSize;
        this.defaultMaxSize = defaultMaxSize;
        this.logicCoreSize = logicCoreSize;
        this.logicMaxSize = logicMaxSize;
        this.virtualCoreSize = virtualCoreSize;
        this.virtualMaxSize = virtualMaxSize;
    }
}
