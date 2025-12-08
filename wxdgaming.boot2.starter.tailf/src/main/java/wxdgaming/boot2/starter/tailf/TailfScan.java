package wxdgaming.boot2.starter.tailf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ExecutorProperties;

/**
 * 扫描文件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-08 13:33
 */
@ComponentScan(basePackageClasses = {
        CoreScan.class,
        ExecutorProperties.class,
        ExecutorFactory.class,
})
@Component
public class TailfScan {
}
