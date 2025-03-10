package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Singleton;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Sort;
import wxdgaming.boot2.core.ann.Value;

/**
 * http 监听 绑定工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:36
 **/
@Slf4j
@Getter
@Singleton
public class HttpListenerFactory {

    /** 相当于用 read and copy write方式作为线程安全性 */
    HttpListenerContent httpListenerContent;

    @Init
    @Sort(7)
    public void init(@Value(path = "http.server", nestedPath = true, required = false) HttpServerConfig httpServerConfig,
                     RunApplication runApplication) {
        httpListenerContent = new HttpListenerContent(httpServerConfig, runApplication);
    }

    public void dispatch(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        httpListenerContent.dispatch(ctx, fullHttpRequest);
    }

}
