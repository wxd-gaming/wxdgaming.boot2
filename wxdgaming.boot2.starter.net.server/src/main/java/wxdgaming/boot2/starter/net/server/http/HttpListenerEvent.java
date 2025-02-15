package wxdgaming.boot2.starter.net.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.RunApplication;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.threading.Event;

/**
 * http 监听 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:29
 **/
@Slf4j
public class HttpListenerEvent extends Event {

    private final HttpMapping httpMapping;
    private final RunApplication runApplication;
    private final HttpContext httpContext;

    public HttpListenerEvent(HttpMapping httpMapping, RunApplication runApplication, HttpContext httpContext) {
        super(httpMapping.method());
        this.httpMapping = httpMapping;
        this.runApplication = runApplication;
        this.httpContext = httpContext;
    }

    @Override public void onEvent() throws Exception {
        if (StringUtils.isNotBlank(httpMapping.httpRequest().method())) {
            if (!httpMapping.httpRequest().method().equalsIgnoreCase(httpContext.getRequest().httpMethod().name())) {
                httpContext.getResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
                httpContext.getResponse().response("method not allowed");
                return;
            }
        }
        httpMapping.invoke(runApplication, httpContext);
    }


}
