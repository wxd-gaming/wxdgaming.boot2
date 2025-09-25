package wxdgaming.game.login.external.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.WebFilter;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.admin.AdminService;

import java.util.Map;

/**
 * 拦截器, 通过参数或者cookie的 authorization 值获取授权信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 15:20
 **/
@Slf4j
@Component
public class AdminFilter implements WebFilter {

    final LoginServerProperties loginServerProperties;
    final AdminService adminService;

    public AdminFilter(LoginServerProperties loginServerProperties, AdminService adminService) {
        this.loginServerProperties = loginServerProperties;
        this.adminService = adminService;
    }


    @Override public String filterPath() {
        return "/admin/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminUserToken adminUserToken = AdminUserToken.parse(request, loginServerProperties.getAdminKey());
        AssertUtil.assertTrue(adminUserToken != null, "token过期");
        ThreadContext.putContent("adminUserToken", adminUserToken);
        String currentUrl = SpringUtil.getCurrentUrl(request);
        String body = SpringUtil.readBody(request);
        Map<String, String> stringStringMap = SpringUtil.readParameterMap(request);
        log.info("{}", """ 
                
                -------------------------------------------------------
                请求日志记录
                   url: %s
                params: (%s)(%s)
                 admin: %s
                -------------------------------------------------------
                """.formatted(currentUrl, body, stringStringMap, adminUserToken));
        return true;
    }

}
