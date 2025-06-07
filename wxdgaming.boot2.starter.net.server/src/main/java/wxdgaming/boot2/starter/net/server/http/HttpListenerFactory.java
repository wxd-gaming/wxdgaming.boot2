package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.ann.Value;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.http.HttpHeadValueType;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

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

    RunApplication runApplication;
    HttpServerConfig httpServerConfig;
    /** 相当于用 read and copy write方式作为线程安全性 */
    HttpListenerContent httpListenerContent;

    @Init
    @Order(7)
    public void init(RunApplication runApplication,
                     @Value(path = "socket.server.http", nestedPath = true, required = false) HttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
        this.runApplication = runApplication;
        this.httpListenerContent = new HttpListenerContent(httpServerConfig, runApplication);
    }

    public void dispatch(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            ThreadContext.cleanup();

            HttpContext httpContext = new HttpContext(this.httpServerConfig, ctx, fullHttpRequest);

            String uriPath = httpContext.getRequest().getUriPath();
            String lowerCase = uriPath.toLowerCase();
            HttpMapping httpMapping = this.httpListenerContent.getHttpMappingMap().get(lowerCase);
            HttpRequest httpRequest = httpMapping == null ? null : httpMapping.httpRequest();
            Method method = httpMapping == null ? null : httpMapping.javassistProxy().getMethod();

            Object filterMatch = this.httpListenerContent.getHttpFilterList().stream()
                    .map(httpFilter -> httpFilter.doFilter(httpRequest, method, uriPath, httpContext))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (filterMatch != null) {
                httpContext.getResponse().response(filterMatch);
                return;
            }
            if (httpMapping == null) {
                HttpListenerTriggerFile httpListenerTriggerFile = new HttpListenerTriggerFile(httpContext);
                ExecutorFactory.getExecutorServiceVirtual().execute(httpListenerTriggerFile);
            } else {
                if (this.httpServerConfig.isShowRequest()) {
                    StringBuilder showLog = httpContext.showLog();
                    log.info("{}", showLog);
                }
                HttpListenerTrigger httpListenerTrigger = new HttpListenerTrigger(httpMapping, runApplication, httpContext);
                httpListenerTrigger.submit();
            }
        } catch (Throwable e) {
            log.error("dispatch error", e);
            RunResult serverError = RunResult.fail("server error");
            ByteBuf byteBuf = Unpooled.wrappedBuffer(serverError.toJSONString().getBytes(StandardCharsets.UTF_8));
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(fullHttpRequest.protocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeadValueType.Json);
            /* TODO 非复用的连接池 */
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            ctx.writeAndFlush(response).addListener(future -> {
                ctx.disconnect();
                ctx.close();
            });
        }
    }
}
