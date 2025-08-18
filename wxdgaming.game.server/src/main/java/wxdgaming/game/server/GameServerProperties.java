package wxdgaming.game.server;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.BootstrapProperties;
import wxdgaming.boot2.core.ann.Configuration;
import wxdgaming.boot2.core.ann.ConfigurationProperties;

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
