package wxdgaming.boot2.starter.net;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-20 19:14
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "socket")
public class SocketProperties {

    SocketServerConfig server;
    SocketServerConfig serverSecond;

    SocketClientConfig client;
    SocketClientConfig clientSecond;

}
