package wxdgaming.game.gateway.module.service;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.Setter;
import wxdgaming.boot2.starter.net.ChannelUtil;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.client.SocketClient;
import wxdgaming.boot2.starter.net.client.SocketClientConfig;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;

import java.util.function.Consumer;

/**
 * 网关到游戏服的连接
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-03 10:13
 **/
@Setter
public class Gateway2GameSocketClientImpl extends SocketClient {

    public Gateway2GameSocketClientImpl(SocketClientConfig config) {
        super(config);
    }

    @Override public void start(ProtoListenerFactory protoListenerFactory) {
        super.start(protoListenerFactory);
    }

    @Override public void init(ProtoListenerFactory protoListenerFactory) {
        super.init(protoListenerFactory);
    }

    @Override protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {
        super.addChanelHandler(socketChannel, pipeline);
        ChannelUtil.attr(socketChannel, "inner-channel", true);
    }

    @Override public ChannelFuture connect(Consumer<SocketSession> consumer) {
        return super.connect((socketSession) -> {
            if (consumer != null) {
                consumer.accept(socketSession);
            }
        });
    }

    @Override public ChannelFuture connect(String inetHost, int inetPort, Consumer<SocketSession> consumer) {
        return super.connect(inetHost, inetPort, consumer);
    }

}
