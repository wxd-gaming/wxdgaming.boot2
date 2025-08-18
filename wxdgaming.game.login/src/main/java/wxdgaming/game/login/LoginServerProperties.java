package wxdgaming.game.login;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;

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
