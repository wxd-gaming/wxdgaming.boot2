package wxdgaming.boot2.starter.scheduled;

import lombok.Setter;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;
import wxdgaming.boot2.core.executor.ExecutorConfig;
import wxdgaming.boot2.core.executor.QueuePolicyConst;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 线程配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-19 11:30
 **/
@Setter
@Configuration
@ConfigurationProperties(prefix = "scheduled")
public class ScheduledProperties extends ObjectBase {

    static final Supplier<ExecutorConfig> DEFAULT_INSTANCE = () -> new ExecutorConfig().setCoreSize(1).setMaxQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);

    private ExecutorConfig executor;

    public ScheduledProperties() {
    }

    public ExecutorConfig getExecutor() {
        if (executor == null) {
            executor = DEFAULT_INSTANCE.get();
        }
        return executor;
    }
}
