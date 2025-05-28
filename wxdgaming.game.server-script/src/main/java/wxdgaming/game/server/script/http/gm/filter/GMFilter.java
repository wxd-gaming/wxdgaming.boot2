package wxdgaming.game.server.script.http.gm.filter;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.boot2.starter.net.server.http.HttpFilter;

import java.lang.reflect.Method;

/**
 * gm权限过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:44
 **/
@Slf4j
@Singleton
public class GMFilter extends HttpFilter {


    @Override public Object doFilter(HttpRequest httpRequest, Method method, String url, HttpContext httpContext) {
        // if (url.startsWith("/gm")) {
        //     if (!BootConfig.getIns().isDebug())
        //         return RunResult.error("功能未开启");
        // }
        return null;
    }

}
