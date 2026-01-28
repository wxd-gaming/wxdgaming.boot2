package wxdgaming.boot2.core.executor;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import wxdgaming.boot2.core.InitPrint;
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
@Getter
@Configuration
@ConfigurationProperties(prefix = "core.executor")
public class ExecutorProperties extends ObjectBase implements InitPrint {

    static final Supplier<ExecutorConfig> BASIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(2).setMaxQueueSize(5000).setWarnQueueSize(500).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    static final Supplier<ExecutorConfig> LOGIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(8).setMaxQueueSize(50000).setWarnQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    static final Supplier<ExecutorConfig> VIRTUAL_INSTANCE = () -> new ExecutorConfig().setCoreSize(200).setMaxQueueSize(50000).setWarnQueueSize(500).setQueuePolicy(QueuePolicyConst.AbortPolicy);

    /** 输出运行日志间隔时间，单位分钟 */
    private int outRunTimeDelay = 15;
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

    @PostConstruct
    public void init() {
        ExecutorFactory.init(this);
    }

}
