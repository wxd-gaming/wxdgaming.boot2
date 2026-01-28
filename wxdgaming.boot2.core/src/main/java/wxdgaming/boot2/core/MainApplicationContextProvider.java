package wxdgaming.boot2.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ExecutorProperties;
import wxdgaming.boot2.core.util.JvmUtil;

/**
 * 运行类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 16:55
 **/
@Slf4j
@Component
public class MainApplicationContextProvider extends ApplicationContextProvider {

    @Autowired
    public MainApplicationContextProvider(ExecutorProperties executorProperties) {
        SpringUtil.mainApplicationContextProvider = this;
    }

    @Override public MainApplicationContextProvider postInitEvent() {
        super.postInitEvent();
        return this;
    }

    @Override public MainApplicationContextProvider postStartEvent() {
        super.postStartEvent();
        return this;
    }

    public MainApplicationContextProvider startBootstrap() {
        postStartEvent();
        BootstrapProperties bootstrapProperties = getBean(BootstrapProperties.class);
        log.info(
                """
                
                        ========================================================================================
                
                                          启动完成 PID:{}, sid:{}, name:{}
                
                        ========================================================================================
                        """,
                JvmUtil.processIDString(), bootstrapProperties.getSid(), bootstrapProperties.getName()
        );
        return this;
    }

}
