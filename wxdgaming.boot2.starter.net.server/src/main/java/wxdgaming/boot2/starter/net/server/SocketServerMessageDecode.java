package wxdgaming.boot2.starter.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ChannelUtil;
import wxdgaming.boot2.starter.net.MessageDecode;
import wxdgaming.boot2.starter.net.SocketSession;
import wxdgaming.boot2.starter.net.pojo.ProtoListenerFactory;
import wxdgaming.boot2.starter.net.server.http.HttpListenerFactory;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-17 15:07
 **/
@Slf4j
@ChannelHandler.Sharable
public class SocketServerMessageDecode extends MessageDecode {

    final SocketServerConfig config;

    public SocketServerMessageDecode(SocketServerConfig config, ProtoListenerFactory protoListenerFactory, HttpListenerFactory httpListenerFactory) {
        super(protoListenerFactory, httpListenerFactory);
        this.config = config;
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override protected void actionWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (!config.isEnabledWebSocket()) {
            ChannelUtil.closeSession(ctx.channel(), "不支持 WebSocket 服务");
            return;
        }
        super.actionWebSocketFrame(ctx, frame);
    }

    @Override protected void actionBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if (!config.isEnabledTcp()) {
            ChannelUtil.closeSession(ctx.channel(), "不支持 tcp socket 服务");
            return;
        }
        super.actionBytes(ctx, byteBuf);
    }

    @Override protected void actionHttpRequest(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        if (!config.isEnabledHttp()) {
            ChannelUtil.closeSession(ctx.channel(), "不支持 Http 服务");
            return;
        }
        super.actionHttpRequest(ctx, httpRequest);
    }

    @Override protected void dispatch(SocketSession socketSession, String messageBytes) throws Exception {
        IServerWebSocketStringListener instance = protoListenerFactory.getServerWebSocketStringListener();
        if (instance != null) {
            instance.onMessage(socketSession, messageBytes);
        } else {
            socketSession.close("不支持 websocket text 文件监听");
        }
    }
}
