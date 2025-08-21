package wxdgaming.game.server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.BootstrapProperties;

/**
 * 游戏服务配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-18 10:41
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "boot")
public class GameServerProperties extends BootstrapProperties {

    private int serverType = 1;

}
