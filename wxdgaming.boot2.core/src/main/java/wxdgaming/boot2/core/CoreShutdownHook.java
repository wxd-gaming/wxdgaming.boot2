package wxdgaming.boot2.core;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.event.StopBeforeEvent;
import wxdgaming.boot2.core.event.StopEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.util.JvmUtil;

import java.util.List;

/**
 * 关闭
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-05 16:43
 **/
@Slf4j
@Component
public class CoreShutdownHook extends HoldApplicationContext {

    @PreDestroy
    public void onCoreShutdownHook() {
        log.info("bean={}", this.getClass().getName());
        SpringUtil.exiting.set(true);
        ExecutorFactory executorFactory = applicationContextProvider.getBean(ExecutorFactory.class);
        applicationContextProvider.postEvent(new StopBeforeEvent());
        applicationContextProvider.postEvent(new StopEvent());
        List<AutoCloseable> list = applicationContextProvider.classWithSuperStream(AutoCloseable.class).toList();
        for (AutoCloseable closeable : list) {
            try {
                log.debug("关闭bean：{}", closeable);
                closeable.close();
            } catch (Exception e) {
                log.error("关闭bean异常...", e);
            }
        }
        executorFactory.getEXECUTOR_MONITOR().getExit().set(true);
        JvmUtil.halt(0);
    }

}
