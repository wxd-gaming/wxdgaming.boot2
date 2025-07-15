package wxdgaming.boot2.starter.net.server.http;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.executor.ExecutorEvent;
import wxdgaming.boot2.core.executor.ExecutorFactory;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.starter.net.http.HttpHeadValueType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            byte[] inputStream = factory.fileInputStream(htmlPath);
            if (inputStream != null) {
                HttpHeadValueType contentType = HttpHeadValueType.findContentType(htmlPath);
                if (factory.getHttpServerConfig().getExperienceSeconds() > 0) {
                    /*如果是固有资源增加缓存效果*/
                    httpContext.getResponse().header(HttpHeaderNames.PRAGMA.toString(), "private");
                    /*过期时间10个小时*/
                    httpContext.getResponse().header(HttpHeaderNames.EXPIRES.toString(), ExpiresFormat.format(new Date(MyClock.addHourOfTime(10))) + " GMT");
                    /*过期时间10个小时*/
                    httpContext.getResponse().header(HttpHeaderNames.CACHE_CONTROL.toString(), "max-age=" + (60 * 60 * 10));
                }
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
                HttpListenerTrigger.execute(factory.getRunApplication(), topHttpMapping, httpContext, factory.getHttpServerConfig().isShowResponse());
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
