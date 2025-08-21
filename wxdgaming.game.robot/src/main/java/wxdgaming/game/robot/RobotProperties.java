package wxdgaming.game.robot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;

/**
 * 网关配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 10:39
 **/
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "boot")
public class RobotProperties extends BootstrapProperties {


}
