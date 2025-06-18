package wxdgaming.boot2.core.executor;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 线程池配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 15:05
 **/
@Getter
public class ExecutorConfig extends ObjectBase {

    public static Supplier<Object> BASIC_INSTANCE = () -> new ExecutorConfig(2, 5000, QueuePolicyConst.AbortPolicy);
    public static Supplier<Object> LOGIC_INSTANCE = () -> new ExecutorConfig(8, 5000, QueuePolicyConst.AbortPolicy);
    public static Supplier<Object> VIRTUAL_INSTANCE = () -> new ExecutorConfig(100, 5000, QueuePolicyConst.AbortPolicy);

    @JSONField(ordinal = 1)
    private final int coreSize;
    @JSONField(ordinal = 3)
    private final int maxQueueSize;
    private final QueuePolicyConst queuePolicy;

    @JSONCreator
    public ExecutorConfig(
            @JSONField(name = "coreSize") Integer coreSize,
            @JSONField(name = "maxQueueSize") Integer maxQueueSize,
            @JSONField(name = "queuePolicy") QueuePolicyConst queuePolicy) {
        this.coreSize = coreSize == null ? 2 : coreSize;
        this.maxQueueSize = maxQueueSize == null ? 5000 : maxQueueSize;
        this.queuePolicy = queuePolicy == null ? QueuePolicyConst.AbortPolicy : queuePolicy;
    }
}
