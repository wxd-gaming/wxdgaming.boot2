package wxdgaming.logserver.module.admin.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.WebFilter;
import wxdgaming.boot2.core.executor.ThreadContext;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.logserver.LogServerProperties;
import wxdgaming.logserver.module.admin.AdminService;

/**
 * 拦截器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 15:20
 **/
@Slf4j
@Component
public class AdminLogFilter implements WebFilter {

    final LogServerProperties logServerProperties;
    final AdminService adminService;

    public AdminLogFilter(LogServerProperties logServerProperties, AdminService adminService) {
        this.logServerProperties = logServerProperties;
        this.adminService = adminService;
    }


    @Override public String filterPath() {
        return "/admin/log/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminUserToken adminUserToken = AdminUserToken.parse(request, logServerProperties.getAdminKey());
        AssertUtil.isTrue(adminUserToken != null, "token过期");
        ThreadContext.putContent("adminUserToken", adminUserToken);
        return true;
    }

}
