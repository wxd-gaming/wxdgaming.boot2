package wxdgaming.boot2.starter.net.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
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

    private static final String HEAD_X_FORWARDED_FOR = "X-Forwarded-For";

    public WebSocketRemoteIp() {
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            HttpHeaders headers = ((FullHttpRequest) msg).headers();
            String clientIp = SpringUtil.getClientIp(headers::get, () -> null);
            if (StringUtils.isNotBlank(clientIp)) {
                ctx.channel().attr(ChannelUtil.WEB_SOCKET_IP_KEY).setIfAbsent(clientIp);
            }
            ctx.pipeline().remove(this.getClass());
        }

        ctx.fireChannelRead(msg);
    }
}
