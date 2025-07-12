package wxdgaming.boot2.starter.net.server.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.cache2.CASCache;
import wxdgaming.boot2.core.cache2.Cache;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.http.HttpHeadValueType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * http 监听 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:29
 **/
@Slf4j
public class HttpListenerTriggerFile extends ExecutorEvent {

    /** 过期时间格式化 */
    public static SimpleDateFormat ExpiresFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
    public static Cache<String, byte[]> cache;

    static {
        cache = CASCache.<String, byte[]>builder()
                .expireAfterWriteMs(TimeUnit.SECONDS.toMillis(30))
                .heartTimeMs(TimeUnit.SECONDS.toMillis(10))
                .loader(HttpListenerTriggerFile::fileInputStream0)
                .build();
        cache.start();
    }

    private static byte[] fileInputStream(String path) {
        if (log.isDebugEnabled()) {
            return fileInputStream0(path);
        }
        return cache.getIfPresent(path);
    }

    private static byte[] fileInputStream0(String path) {
        Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(HttpListenerTriggerFile.class.getClassLoader(), path);
        return inputStream != null ? inputStream.getRight() : null;
    }

    public static void execute(HttpListenerFactory factory, HttpMapping topHttpMapping, HttpContext httpContext) {
        HttpListenerTriggerFile httpListenerTriggerFile = new HttpListenerTriggerFile(factory, topHttpMapping, httpContext);
        ExecutorFactory.getExecutorServiceVirtual().execute(httpListenerTriggerFile);
    }

    private final HttpListenerFactory factory;
    private final HttpMapping topHttpMapping;
    private final HttpContext httpContext;

    public HttpListenerTriggerFile(HttpListenerFactory factory, HttpMapping topHttpMapping, HttpContext httpContext) {
        super();
        this.factory = factory;
        this.topHttpMapping = topHttpMapping;
        this.httpContext = httpContext;
    }

    @Override public void onEvent() throws Exception {
        String htmlPath = "html" + httpContext.getRequest().getUriPath();
        try {
            byte[] inputStream = fileInputStream(htmlPath);
            if (inputStream != null) {
                HttpHeadValueType contentType = HttpHeadValueType.findContentType(htmlPath);
                /*如果是固有资源增加缓存效果*/
                httpContext.getResponse().header(HttpHeaderNames.PRAGMA.toString(), "private");
                /*过期时间10个小时*/
                httpContext.getResponse().header(HttpHeaderNames.EXPIRES.toString(), ExpiresFormat.format(new Date(MyClock.addHourOfTime(10))) + " GMT");
                /*过期时间10个小时*/
                httpContext.getResponse().header(HttpHeaderNames.CACHE_CONTROL.toString(), "max-age=" + (60 * 60 * 10));
                httpContext.getResponse().setResponseContentType(contentType);
                if (httpContext.getHttpServerConfig().isShowResponse()) {
                    StringBuilder stringBuilder = httpContext.showLog();
                    stringBuilder
                            .append("\n=============================================输出================================================")
                            .append("\n").append(HttpHeaderNames.CONTENT_TYPE).append("=").append(contentType)
                            .append("\n")
                            .append(HttpHeaderNames.CONTENT_LENGTH).append("=").append(inputStream.length)
                            .append("\n")
                            .append("file path = ").append(new File(htmlPath).getCanonicalPath())
                            .append("\n=============================================结束================================================")
                            .append("\n");
                    log.debug(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }
                httpContext.getResponse().response(inputStream);
            } else if (topHttpMapping != null) {
                /*顶级监听*/
                HttpListenerTrigger.execute(factory.runApplication, topHttpMapping, httpContext, factory.getHttpServerConfig().isShowResponse());
            } else {
                httpContext.getResponse().setStatus(HttpResponseStatus.NOT_FOUND);
                httpContext.getResponse().response("not found url " + httpContext.getRequest().getUriPath());
            }
        } catch (Exception e) {
            final String ofString = Throw.ofString(e);
            StringBuilder stringBuilder = httpContext.showLog();
            stringBuilder
                    .append(";\n=============================================输出================================================")
                    .append("\nfile path = ").append(new File(htmlPath).getCanonicalPath())
                    .append("\n")
                    .append(ofString)
                    .append("\n=============================================结束================================================")
                    .append("\n");
            log.warn(stringBuilder.toString());
            stringBuilder.setLength(0);
            httpContext.getResponse().setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            httpContext.getResponse().response("server error");
        } finally {
            httpContext.close();
        }
    }

}
