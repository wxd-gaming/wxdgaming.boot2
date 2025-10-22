package wxdgaming.boot2.starter.net;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
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
    public SocketServer socketServer(ProtoListenerFactory protoListenerFactory) {
        return new SocketServer(socketProperties.getServer(), protoListenerFactory);
    }

    @Bean("socket.server-second")
    @ConditionalOnProperty(name = "socket.server-second.port")
    public SocketServer socketServer2(ProtoListenerFactory protoListenerFactory) {
        return new SocketServer(socketProperties.getServer(), protoListenerFactory);
    }

    @Bean()
    @ConditionalOnProperty(name = "socket.client.port")
    public SocketClient socketClient(ProtoListenerFactory protoListenerFactory) {
        return new SocketClient(socketProperties.getClient(), protoListenerFactory);
    }

    @Bean("socket.client-second")
    @ConditionalOnProperty(name = "socket.client-second.port")
    public SocketClient socketClient2(ProtoListenerFactory protoListenerFactory) {
        return new SocketClient(socketProperties.getClientSecond(), protoListenerFactory);
    }

}
