package wxdgaming.boot2.starter.groovy;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 09:22
 **/
@ComponentScan(basePackageClasses = {CoreScan.class, GroovyService.class})
@Component
public class GroovyScan {
}
