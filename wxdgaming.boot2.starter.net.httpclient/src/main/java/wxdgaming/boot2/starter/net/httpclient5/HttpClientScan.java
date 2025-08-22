package wxdgaming.boot2.starter.net.httpclient5;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.CoreScan;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-22 13:35
 **/
@ComponentScan(basePackageClasses = {CoreScan.class, HttpClientProperties.class, HttpClientConfiguration.class})
@Component
public class HttpClientScan {
}
