package wxdgaming.game.login;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 11:08
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class LoginServerProperties extends BootstrapProperties {

    private String jwtKey;

}
