package code;

import io.netty.channel.ChannelFuture;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class WsClientTest {

    public static void main(String[] args) throws Exception {
        SocketClientConfig socketClientConfig = new SocketClientConfig();
        socketClientConfig.setEnabledSSL(false);
        socketClientConfig.setEnabledWebSocket(true);
        socketClientConfig.setHost("s1-tsjlbb-910494yq712.shengaowl.com");
        socketClientConfig.setPort(8101);
        SocketClient socketClient = new SocketClient(socketClientConfig);
        socketClient.init(new ProtoListenerFactory());
        socketClient.connect(session->{
            System.out.println(session.isOpen());
        });
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        SocketSession idle = socketClient.idle();
        ChannelFuture future = idle.write("ddddd");
        future.addListener(listener->{
            System.out.println(listener.isSuccess());
        }).get();
        System.out.println("sssss");
    }

}
