package wxdgaming.boot2.starter.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.BytesUnit;
import wxdgaming.boot2.starter.net.ChannelUtil;
import wxdgaming.boot2.starter.net.MessageDecode;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;

/**
 * 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 15:07
 **/
@Slf4j
@ChannelHandler.Sharable
public class ServerMessageDecode extends MessageDecode {

    final SocketServerConfig config;

    public ServerMessageDecode(SocketServerConfig config, ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        super(protoListenerFactory, httpListenerFactory);
        this.config = config;
    }

    @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        /*TODO 构造函数自动注册*/
        SocketSession socketSession = new SocketSession(
                SocketSession.Type.server,
                ctx.channel(),
                ChannelUtil.attr(ctx.channel(), ChannelUtil.WEB_SOCKET_SESSION_KEY)
        );
        if (config.getMaxFrameBytes() > 0) {
            socketSession.setMaxFrameBytes(BytesUnit.Mb.toBytes(config.getMaxFrameBytes()));
        }
        socketSession.setMaxFrameLength(config.getMaxFrameLength());
    }

    @Override protected void actionWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (!config.isEnabledWebSocket()) {
            if (log.isWarnEnabled()) {
                log.warn("{} 不支持 WebSocket 服务 {}", ChannelUtil.ctxTostring(ctx), frame.getClass().getName());
            }
            ctx.disconnect();
            ctx.close();
            return;
        }
        super.actionWebSocketFrame(ctx, frame);
    }

    @Override protected void actionBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if (!config.isEnabledTcp()) {
            if (log.isWarnEnabled()) {
                log.warn("{} 不支持 tcp socket 服务 {}", ChannelUtil.ctxTostring(ctx), byteBuf.getClass().getName());
            }
            ctx.disconnect();
            ctx.close();
            return;
        }
        super.actionBytes(ctx, byteBuf);
    }

    @Override protected void actionHttpRequest(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (!config.isEnabledHttp()) {
            if (log.isWarnEnabled()) {
                log.warn("{} 不支持 Http 服务 {}", ChannelUtil.ctxTostring(ctx), httpRequest.uri());
            }
            ctx.disconnect();
            ctx.close();
            return;
        }
        super.actionHttpRequest(ctx, httpRequest);
    }

}
