package wxdgaming.boot2.core.executor;

import lombok.Setter;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 线程配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 11:30
 **/
@Order(value = Integer.MIN_VALUE)
@Setter
@Configuration
@ConfigurationProperties(prefix = "core.executor")
public class ExecutorProperties extends ObjectBase implements InitPrint {

    static final Supplier<ExecutorConfig> BASIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(2).setMaxQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    static final Supplier<ExecutorConfig> LOGIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(8).setMaxQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    static final Supplier<ExecutorConfig> VIRTUAL_INSTANCE = () -> new ExecutorConfig().setCoreSize(200).setMaxQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);

    private ExecutorConfig basic;
    private ExecutorConfig logic;
    private ExecutorConfig virtual;

    public ExecutorConfig getBasic() {
        if (basic == null) {
            basic = BASIC_INSTANCE.get();
        }
        return basic;
    }

    public ExecutorConfig getLogic() {
        if (logic == null) {
            logic = LOGIC_INSTANCE.get();
        }
        return logic;
    }

    public ExecutorConfig getVirtual() {
        if (virtual == null) {
            virtual = VIRTUAL_INSTANCE.get();
        }
        return virtual;
    }
}
