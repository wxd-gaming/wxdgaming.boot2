package wxdgaming.boot2.starter.net.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.starter.net.ChannelUtil;

/**
 * 获取ip
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-12-03 09:09
 **/
@Slf4j
public class WebSocketRemoteIp extends ChannelInboundHandlerAdapter {

    private final int maxContentLength;

    public WebSocketRemoteIp(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest httpRequest) {
            HttpHeaders headers = httpRequest.headers();
            String clientIp = SpringUtil.getClientIp(headers::get, () -> null);
            if (StringUtils.isNotBlank(clientIp)) {
                ctx.channel().attr(ChannelUtil.WEB_SOCKET_IP_KEY).setIfAbsent(clientIp);
            }
            ctx.pipeline().remove(this.getClass());

            if (httpRequest.decoderResult().isSuccess() && "websocket".equalsIgnoreCase(httpRequest.headers().get("Upgrade"))) {
                //用于处理websocket, /ws为访问websocket时的uri
                String uri = httpRequest.uri();
                log.debug("websocket-uri:channel:{}, uri:{}", ctx.channel(), uri);
                // 用于处理websocket, /ws为访问websocket时的uri
                WebSocketServerProtocolHandler webSocketServerProtocolHandler = new WebSocketServerProtocolHandler(uri, null, false, maxContentLength) {

                    @Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        log.debug("{} websocket event triggered {}", ctx.channel(), evt);
                        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                            ChannelUtil.session(ctx.channel()).setHandshake_complete(true);
                        }
                        super.userEventTriggered(ctx, evt);
                    }

                };
                ctx.pipeline().addBefore("device-handler", "ProtocolHandler", webSocketServerProtocolHandler);
                ChannelUtil.session(ctx.channel()).setWebSocket(true);
            }

        }

        ctx.fireChannelRead(msg);
    }
}
