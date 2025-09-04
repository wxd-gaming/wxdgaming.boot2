package wxdgaming.boot2.starter.event;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 10:10
 **/
@ComponentScan(basePackageClasses={
        CoreScan.class,
        EventService.class,
})
@Component
public class EventScan {
}
