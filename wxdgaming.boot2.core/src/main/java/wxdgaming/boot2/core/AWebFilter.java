package wxdgaming.boot2.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wxdgaming.boot2.core.executor.ExecutorContext;
import wxdgaming.boot2.core.timer.MyClock;

/**
 * 过滤器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 13:44
 **/
@Slf4j
@Component
public class AWebFilter implements HandlerInterceptor, WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this);
        interceptorRegistration.addPathPatterns("/**"); // 拦截的路径
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ExecutorContext.Content context = new ExecutorContext.Content() {
            @Override public long getExecutorWarnTime() {
                return 1000;
            }

            @Override public long getSubmitWarnTime() {
                return 3000;
            }
        };
        context.setThread(Thread.currentThread());
        context.setNewTime(System.nanoTime());
        context.setActualNewTime(MyClock.millis());
        context.running(request.getRequestURI());
        ExecutorContext.setContext(context);
        return true;
    }

    @Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ExecutorContext.cleanup();
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ExecutorContext.release();
    }
}
