package wxdgaming.boot2.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 拦截器传递
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 16:19
 */
@Component
public class CacheHttpServletRequestFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        //顺序控制要看你自己的代码
        //尽量小，比如说我这里是OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER-106
        //REQUEST_WRAPPER_FILTER_MAX_ORDER变量是spring 官方推荐的顺序
        //但是直接使用可能也会有坑，你可以自己查一下。
        //因为有一个spring boot 默认扩展的过滤OrderedRequestContextFilter
        //它使用的是REQUEST_WRAPPER_FILTER_MAX_ORDER - 105
        //所以为了尽可能早一点，你自己根据你的情况调整顺序
        return OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 106;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            //传递包装类下去。这样后面的servlet等可以拿到这个包装后的request
            CacheHttpServletRequest requestWrapperNew = new CacheHttpServletRequest(httpServletRequest);
            String contentType = request.getContentType();
            if ("POST".equalsIgnoreCase(httpServletRequest.getMethod()) && StringUtils.isNotBlank(contentType) && contentType.contains("application/json")) {
                /*TODO 这个代码不能删除，必须强制读取一次才行*/
                String string = requestWrapperNew.getReader().readLine();
            }
            chain.doFilter(requestWrapperNew, response);
            return;
        }
        chain.doFilter(request, response);
    }

}
