package wxdgaming.minitieba.filter;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wxdgaming.boot2.core.token.JsonTokenParse;
import wxdgaming.minitieba.MinitiebaProperties;
import wxdgaming.minitieba.bean.User;
import wxdgaming.minitieba.service.UserService;

/**
 * 认证拦截器，拦截需要登录的接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-04-26
 **/
@Slf4j
@Component
public class AuthFilter implements HandlerInterceptor, WebMvcConfigurer {

    private final MinitiebaProperties properties;
    private final UserService userService;

    public AuthFilter(MinitiebaProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(this);
        registration.addPathPatterns(
                "/api/post/create",
                "/api/post/reply",
                "/api/post/like",
                "/api/post/notices",
                "/api/post/notices/read",
                "/api/file/upload"
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            sendError(response, "请先登录");
            return false;
        }

        try {
            wxdgaming.boot2.core.token.JsonToken jsonToken = JsonTokenParse.parse(properties.getJwtKey(), token);
            if (jsonToken == null) {
                sendError(response, "Token无效");
                return false;
            }

            String username = jsonToken.getString("username");

            // 检查用户是否还存在
            User user = userService.getUser(username);
            if (user == null) {
                sendError(response, "用户已被删除，请重新注册");
                return false;
            }

            // 将用户信息放入request属性，供Controller使用
            request.setAttribute("currentUser", username);
            request.setAttribute("currentNickname", user.getNickname());
            return true;
        } catch (IllegalArgumentException e) {
            sendError(response, "Token已过期，请重新登录");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        result.put("code", 401);
        result.put("msg", message);
        response.getWriter().write(result.toJSONString());
    }

}
