package wxdgaming.boot2.starter.net.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;

import java.net.URI;
import java.util.function.Consumer;

/**
 * tcp client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 13:12
 **/
@Slf4j
@Getter
@Accessors(chain = true)
public class WebSocketClient extends SocketClient {

    private WebSocketClientHandshaker handshaker;
    /** 包含的http head参数 */
    protected final HttpHeaders httpHeaders = new DefaultHttpHeaders();

    public WebSocketClient(SocketClientConfig config) {
        super(config);
    }

    @Start
    @Override public void start(ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        String protocol = "ws";
        if (config.isEnabledSSL()) {
            protocol = "wss";
        }
        String url = protocol + "://" + getConfig().getHost() + ":" + getConfig().getPort() + this.getConfig().getWebSocketPrefix();
        log.debug("{}", url);
        handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                URI.create(url),
                WebSocketVersion.V13,
                null,
                false,
                httpHeaders,
                (int) BytesUnit.Mb.toBytes(64)/*64 mb*/
        );
        super.start(protoListenerFactory, httpListenerFactory);
    }

    @Override protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {
        super.addChanelHandler(socketChannel, pipeline);
        int maxContentLength = (int) BytesUnit.Mb.toBytes(config.getMaxAggregatorLength());
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addBefore("device-handler", "http-codec", new HttpClientCodec());
        pipeline.addBefore("device-handler", "http-object-aggregator", new HttpObjectAggregator(maxContentLength));/*接受完整的http消息 64mb*/
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        pipeline.addBefore("device-handler", "http-chunked", new ChunkedWriteHandler());
        pipeline.addBefore("device-handler", "websocket-aggregator", new WebSocketFrameAggregator(maxContentLength));
        pipeline.addBefore("device-handler", "ProtocolHandler", new WebSocketClientProtocolHandler(handshaker));
        // handshaker.handshake(socketChannel);
    }

    @Override public ChannelFuture connect(Consumer<SocketSession> consumer) {
        return super.connect(socketSession -> {
            socketSession.setSsl(config.isEnabledSSL());
            socketSession.setWebSocket(true);
            if (consumer != null) {
                consumer.accept(socketSession);
            }
        });
    }

}
