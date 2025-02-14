package wxdgaming.boot2.starter.net.server.http;

import com.google.inject.Singleton;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.Throw;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.io.FileUtil;
import wxdgaming.boot2.core.io.Objects;
import wxdgaming.boot2.core.lang.Tuple2;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.boot2.core.util.AnnUtil;
import wxdgaming.boot2.starter.net.http.HttpHeadValueType;
import wxdgaming.boot2.starter.net.server.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.ann.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

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
    /** 过期时间格式化 */
    public static SimpleDateFormat ExpiresFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final ConcurrentHashMap<String, HttpMapping> httpMappingMap = new ConcurrentHashMap<>();

    RunApplication lastRunApplication;

    @Init
    public void init(RunApplication runApplication) {
        lastRunApplication = runApplication;
        runApplication.getReflectContext()
                .withMethodAnnotated(HttpRequest.class)
                .sorted()
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    HttpRequest methodRequestMapping = AnnUtil.ann(method, HttpRequest.class);

                    String path = "";

                    if (insRequestMapping != null) {
                        path += insRequestMapping.path();
                    } else {
                        String simpleName = ins.getClass().getSimpleName();
                        if (simpleName.endsWith("Spi")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        } else if (simpleName.endsWith("Impl")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 4);
                        } else if (simpleName.endsWith("Service")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 7);
                        } else if (simpleName.endsWith("Controller")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 10);
                        } else if (simpleName.endsWith("Api")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        }

                        path += simpleName + "/";
                    }

                    if (StringUtils.isBlank(methodRequestMapping.path())) {
                        path += method.getName();
                    } else {
                        path += methodRequestMapping.path();
                    }

                    String lowerCase = path.toLowerCase();
                    HttpMapping httpMapping = new HttpMapping(methodRequestMapping, lowerCase, ins, method);

                    HttpMapping old = httpMappingMap.put(lowerCase, httpMapping);
                    if (old != null && !Objects.equals(old.ins().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s old = %s - new = %s"
                                .formatted(
                                        lowerCase,
                                        old.ins().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("http listener url: {}", lowerCase);
                });
    }

    public void dispatch(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            HttpContext httpContext = new HttpContext(ctx, fullHttpRequest);
            String uriPath = httpContext.getRequest().getUriPath();
            String lowerCase = uriPath.toLowerCase();
            HttpMapping httpMapping = httpMappingMap.get(lowerCase);
            if (httpMapping == null) {
                if (actionFile(httpContext)) {
                    return;
                }
                httpContext.getResponse().setStatus(HttpResponseStatus.NOT_FOUND);
                httpContext.getResponse().response("not found url " + httpContext.getRequest().getUriPath());
            } else {
                if (StringUtils.isNotBlank(httpMapping.httpRequest().method())) {
                    if (!httpMapping.httpRequest().method().equalsIgnoreCase(httpContext.getRequest().httpMethod().name())) {
                        httpContext.getResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
                        httpContext.getResponse().response("method not allowed");
                        return;
                    }
                }
                httpMapping.invoke(lastRunApplication, httpContext);
            }
        } catch (Exception e) {
            log.error("dispatch error", e);
        }
    }

    public boolean actionFile(HttpContext httpContext) throws IOException {
        String htmlPath = "html" + httpContext.getRequest().getUriPath();
        try {
            Tuple2<Path, byte[]> inputStream = FileUtil.findInputStream(this.getClass().getClassLoader(), htmlPath);
            if (inputStream != null) {
                HttpHeadValueType hct = HttpHeadValueType.findContentType(htmlPath);
                /*如果是固有资源增加缓存效果*/
                httpContext.getResponse().header(HttpHeaderNames.PRAGMA.toString(), "private");
                /*过期时间10个小时*/
                httpContext.getResponse().header(HttpHeaderNames.EXPIRES.toString(), ExpiresFormat.format(new Date(MyClock.addHourOfTime(10))) + " GMT");
                /*过期时间10个小时*/
                httpContext.getResponse().header(HttpHeaderNames.CACHE_CONTROL.toString(), "max-age=" + (60 * 60 * 10));
                httpContext.getResponse().response(inputStream.getRight());
                return true;
            }
            return false;
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
            return true;
        }
    }
}
