package wxdgaming.game.server.script.http.yunying.filter;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.boot2.starter.net.server.http.HttpFilter;

import java.lang.reflect.Method;

/**
 * 运营过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:36
 **/
@Slf4j
@Singleton
public class YunyingFilter extends HttpFilter {


    @Override public Object doFilter(HttpRequest httpRequest, Method method, HttpContext httpContext) {
        if (httpContext.getRequest().getUriPath().startsWith("/yunying")) {
            // 这里可以添加运营权限验证逻辑
            return null;
        }
        return null;
    }

}
