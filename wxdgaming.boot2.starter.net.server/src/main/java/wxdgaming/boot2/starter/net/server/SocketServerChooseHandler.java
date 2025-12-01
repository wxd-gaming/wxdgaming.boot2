package wxdgaming.boot2.starter.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.starter.net.ChannelUtil;

import java.util.List;
import java.util.Set;

/**
 * 判定是 socket 还是 web socket
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2023-08-25 09:47
 **/
@Slf4j
public class SocketServerChooseHandler extends ByteToMessageDecoder {

    /** 默认暗号长度为23 */
    private static final int MAX_LENGTH = 23;
    /** WebSocket握手的协议前缀 */
    private static final Set<String> WEB_PREFIX = Set.of(
            "GET /",
            "POST /",
            "OPTIONS /",
            "HEAD /",
            "PUT /",
            "PATCH /",
            "DELETE /",
            "TRACE /",
            "CONNECT /"
    );

    final SocketServerConfig socketServerConfig;

    public SocketServerChooseHandler(SocketServerConfig socketServerConfig) {
        this.socketServerConfig = socketServerConfig;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String protocol = getBufStart(in);
        if (WEB_PREFIX.stream().anyMatch(protocol::startsWith)) {
            websocketAdd(ctx);
        }
        in.resetReaderIndex();
        ctx.pipeline().remove(this.getClass());
    }

    private String getBufStart(ByteBuf in) {
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }
        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        return new String(content);
    }

    public void websocketAdd(ChannelHandlerContext ctx) {
        int maxContentLength = (int) BytesUnit.Mb.toBytes(socketServerConfig.getMaxAggregatorLength());
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        ctx.pipeline().addBefore("device-handler", "http-codec", new HttpServerCodec());
        // 添加HTTP内容解压缩器，用于处理Gzip压缩的请求
        ctx.pipeline().addBefore("device-handler", "Http-Content-Decompressor", new HttpContentDecompressor(maxContentLength));
        /*接受完整的http消息 64mb*/
        ctx.pipeline().addBefore("device-handler", "http-object-aggregator", new HttpObjectAggregator(maxContentLength));
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        ctx.pipeline().addBefore("device-handler", "http-chunked", new ChunkedWriteHandler());
        /*接受完整的websocket消息 64mb*/
        ctx.pipeline().addBefore("device-handler", "WebSocketAggregator", new WebSocketFrameAggregator(maxContentLength));
        // 用于处理websocket, /ws为访问websocket时的uri
        WebSocketServerProtocolHandler webSocketServerProtocolHandler = new WebSocketServerProtocolHandler(socketServerConfig.getWebSocketPrefix(), null, false, maxContentLength) {

            @Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                log.debug("{} websocket event triggered {}", ctx.channel(), evt);
                if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                    ChannelUtil.session(ctx.channel()).setHandshake_complete(true);
                }
                super.userEventTriggered(ctx, evt);
            }
        };
        ctx.pipeline().addBefore("device-handler", "ProtocolHandler", webSocketServerProtocolHandler);
        ChannelUtil.session(ctx.channel()).setWebSocket(true);
    }

}
