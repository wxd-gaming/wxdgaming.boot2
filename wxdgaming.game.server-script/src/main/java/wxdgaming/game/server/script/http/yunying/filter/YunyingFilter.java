package wxdgaming.game.server.script.http.yunying.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.WebFilter;

/**
 * 运营过滤器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:36
 **/
@Slf4j
@Component
public class YunyingFilter implements WebFilter {

    @Override public String filterPath() {
        return "/yunying/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return WebFilter.super.preHandle(request, response, handler);
    }


}
