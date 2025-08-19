package wxdgaming.boot2.core.executor;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.boot2.core.lang.ObjectBase;

/**
 * 线程池配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 15:05
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ExecutorConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private int coreSize;
    @JSONField(ordinal = 2)
    private int maxQueueSize;
    @JSONField(ordinal = 3)
    private QueuePolicyConst queuePolicy = QueuePolicyConst.AbortPolicy;

}
