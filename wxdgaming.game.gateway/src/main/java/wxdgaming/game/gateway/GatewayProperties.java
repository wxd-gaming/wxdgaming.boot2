package wxdgaming.game.gateway;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;

/**
 * 网关配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 10:39
 **/
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "boot")
public class GatewayProperties extends BootstrapProperties {


}
