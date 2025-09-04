package wxdgaming.boot2.starter.net;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 09:55
 **/
@ComponentScan(basePackageClasses = {CoreScan.class, SocketProperties.class, SocketConfiguration.class})
@Component
public class SocketScan {
}
