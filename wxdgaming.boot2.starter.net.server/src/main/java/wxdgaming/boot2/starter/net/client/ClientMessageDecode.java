package wxdgaming.boot2.starter.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ChannelUtil;
import wxdgaming.boot2.starter.net.MessageDecode;
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
public class ClientMessageDecode extends MessageDecode {

    final SocketClientConfig socketClientConfig;


    public ClientMessageDecode(SocketClientConfig socketClientConfig, ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        super(protoListenerFactory, httpListenerFactory);
        this.socketClientConfig = socketClientConfig;
    }

    @Override protected void actionWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (!socketClientConfig.isEnabledWebSocket()) {
            if (log.isDebugEnabled()) {
                log.debug("{} 不支持 WebSocket 服务 {}", ChannelUtil.ctxTostring(ctx), frame.getClass().getName());
            }
            ctx.disconnect();
            ctx.close();
            return;
        }
        super.actionWebSocketFrame(ctx, frame);
    }

    @Override protected void actionBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if (socketClientConfig.isEnabledWebSocket()) {
            if (log.isDebugEnabled()) {
                log.debug("{} 不支持 tcp socket 服务 {}", ChannelUtil.ctxTostring(ctx), byteBuf.getClass().getName());
            }
            ctx.disconnect();
            ctx.close();
            return;
        }
        super.actionBytes(ctx, byteBuf);
    }

    @Override protected void actionHttpRequest(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("{} 不支持 Http 服务 {}", ChannelUtil.ctxTostring(ctx), httpRequest.uri());
        }
        ctx.disconnect();
        ctx.close();
    }

}
