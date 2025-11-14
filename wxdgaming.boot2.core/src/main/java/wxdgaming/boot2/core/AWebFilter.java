package wxdgaming.boot2.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wxdgaming.boot2.core.executor.ThreadStopWatch;

import java.util.concurrent.TimeUnit;

/**
 * 过滤器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 13:44
 **/
@Component
public class AWebFilter implements HandlerInterceptor, WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this);
        interceptorRegistration.addPathPatterns("/**"); // 拦截的路径
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ThreadStopWatch.initNotPresent(TimeUnit.MICROSECONDS,request.getRequestURI());
        return true;
    }

    @Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ThreadStopWatch.releasePrint();
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadStopWatch.release();
    }
}
