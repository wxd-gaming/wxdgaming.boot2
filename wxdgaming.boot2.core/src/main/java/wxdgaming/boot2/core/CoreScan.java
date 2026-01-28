package wxdgaming.boot2.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.executor.ExecutorProperties;

/**
 * 核心模块扫描
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-17 13:36
 **/
@ComponentScan(basePackageClasses = {
        ExecutorProperties.class,
        CoreScan.class,
})
@Component
public class CoreScan {
}
