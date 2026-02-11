package wxdgaming.boot2.starter.js;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-04 20:21
 **/
@ComponentScan(basePackageClasses = {CoreScan.class, JsService.class})
@Component
public class JsScan {
}
