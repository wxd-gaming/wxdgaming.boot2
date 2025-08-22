package wxdgaming.boot2.starter.net;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.core.CoreScan;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.server.SocketServer;

/**
 * socket 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 09:45
 **/
@Slf4j
@Configuration
public class SocketConfiguration {

    final SocketProperties socketProperties;

    public SocketConfiguration(SocketProperties socketProperties) {
        this.socketProperties = socketProperties;
    }

    @Bean
    @ConditionalOnProperty(name = "socket.server.port")
    public SocketServer socketServer() {
        return new SocketServer(socketProperties.getServer());
    }

    @Bean("socket.server-second")
    @ConditionalOnProperty(name = "socket.server-second.port")
    public SocketServer socketServer2() {
        return new SocketServer(socketProperties.getServer());
    }

    @Bean()
    @ConditionalOnProperty(name = "socket.client.port")
    public SocketClient socketClient() {
        return new SocketClient(socketProperties.getClient());
    }

    @Bean("socket.client-second")
    @ConditionalOnProperty(name = "socket.client-second.port")
    public SocketClient socketClient2() {
        return new SocketClient(socketProperties.getClientSecond());
    }

}
