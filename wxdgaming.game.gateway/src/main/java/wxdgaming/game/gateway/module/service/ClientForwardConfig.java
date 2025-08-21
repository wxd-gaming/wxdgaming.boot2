package wxdgaming.game.gateway.module.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 20:30
 **/
@Configuration
@ConfigurationProperties(prefix = "socket.client-forward")
public class ClientForwardConfig extends SocketClientConfig {
}
