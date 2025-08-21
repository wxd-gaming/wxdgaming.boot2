package wxdgaming.boot2.core;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 过滤器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 13:44
 **/
public interface WebFilter extends HandlerInterceptor, WebMvcConfigurer {

    String filterPath();

    @Override
    default void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this);
        interceptorRegistration.addPathPatterns(filterPath()); // 拦截的路径
        //        interceptorRegistration.excludePathPatterns("/api/login"); // 排除的路径
    }

}
