package wxdgaming.logserver.module.admin.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.logserver.module.admin.AdminService;

/**
 * 账户
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 19:57
 **/
@Slf4j
@RestController
@RequestMapping("/web/account")
public class AccountController implements InitPrint {

    final AdminService adminService;

    public AccountController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping("/login")
    public ResponseEntity<RunResult> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        AssertUtil.assertTrue(StringUtils.isNotBlank(userName), "用户名输入不正确");
        AssertUtil.assertTrue(StringUtils.isNotBlank(password), "密码输入不正确");
        return adminService.login(userName, password);
    }

    @RequestMapping("/check")
    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        return adminService.check(request);
    }

}
