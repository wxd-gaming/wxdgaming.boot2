package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Order;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.cache2.Cache;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.http.HttpHeadValueType;
import wxdgaming.boot2.starter.net.server.SocketServerConfig;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * http 监听 绑定工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 16:36
 **/
@Slf4j
@Getter
@Singleton
public class HttpListenerFactory extends HoldRunApplication {

    HttpServerConfig httpServerConfig;
    /** 相当于用 read and copy write方式作为线程安全性 */
    HttpListenerContent httpListenerContent;
    Cache<String, byte[]> cache = null;

    protected byte[] fileInputStream(String path) {
        if (log.isDebugEnabled() || cache == null) {
            return fileInputStream0(path);
        }
        return cache.getIfPresent(path);
    }

    protected byte[] fileInputStream0(String path) {
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(HttpListenerTriggerFile.class.getClassLoader(), path);
        return inputStream != null ? inputStream.getRight() : null;
    }

    @Init
    @Order(7)
    public void init(@Named("socket.server.config") SocketServerConfig serverConfig,
                     @Named("socket.server.http.config") HttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
        this.httpListenerContent = new HttpListenerContent(runApplication, serverConfig);
        int experienceSeconds = httpServerConfig.getExperienceSeconds();
        if (cache == null && experienceSeconds > 0) {
            cache = CASCache.<String, byte[]>builder()
                    .expireAfterWriteMs(TimeUnit.SECONDS.toMillis(experienceSeconds))
                    .heartTimeMs(TimeUnit.SECONDS.toMillis(experienceSeconds / 2))
                    .loader(this::fileInputStream0)
                    .build();
            cache.start();
        }
    }

    public void dispatch(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            ThreadContext.cleanup();

            HttpContext httpContext = new HttpContext(this.httpServerConfig, ctx, fullHttpRequest);
            HttpMapping topHttpMapping = this.httpListenerContent.getTopHttpMapping();
            HttpMapping httpMapping = this.httpListenerContent.getHttpMapping(httpContext);
            HttpRequest httpRequest = httpMapping == null ? null : httpMapping.httpRequest();
            Method method = httpMapping == null ? null : httpMapping.javassistProxy().getMethod();

            Object filterMatch = this.httpListenerContent.getHttpFilterList().stream()
                    .map(httpFilter -> httpFilter.doFilter(httpRequest, method, httpContext))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (filterMatch != null) {
                httpContext.getResponse().response(filterMatch);
                return;
            }
            if (httpMapping == null) {
                HttpListenerTriggerFile.execute(this, topHttpMapping, httpContext);
            } else {
                HttpListenerTrigger.execute(runApplication, httpMapping, httpContext, this.httpServerConfig.isShowRequest());
            }
        } catch (Throwable e) {
            log.error("dispatch error", e);
            RunResult serverError = RunResult.fail("server error:" + e.getMessage());
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
