package wxdgaming.game.login.admin;

import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.AdminUserToken;
import wxdgaming.game.login.entity.AdminUser;
import wxdgaming.game.util.SignUtil;

import java.util.concurrent.TimeUnit;

/**
 * 管理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 15:17
 **/
@Slf4j
@Service
public class AdminService implements InitPrint {

    final LoginServerProperties loginServerProperties;
    final SqlDataHelper sqlDataHelper;

    public AdminService(LoginServerProperties loginServerProperties, PgsqlDataHelper pgsqlDataHelper) {
        this.loginServerProperties = loginServerProperties;
        this.sqlDataHelper = pgsqlDataHelper;
    }

    ResponseEntity<RunResult> buildResponse(AdminUser adminUser) {
        AdminUserToken adminUserToken = new AdminUserToken();
        adminUserToken.setUserName(adminUser.getUserName());

        String jsonToken = JsonTokenBuilder.of(loginServerProperties.getJwtKey(), TimeUnit.DAYS, 7)
                .put("user", adminUserToken)
                .compact();

        Cookie cookie = new Cookie(HttpHeaderNames.AUTHORIZATION.toString(), jsonToken);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(RunResult.ok().data(jsonToken));
    }

    public ResponseEntity<RunResult> login(String userName, String password) {
        AdminUser adminUser = sqlDataHelper.getCacheService().cacheIfPresent(AdminUser.class, userName);
        if (adminUser == null) {
            return ResponseEntity.ok(RunResult.fail("用户不存在"));
        }

        String string = SignUtil.signByJsonKey(password, loginServerProperties.getJwtKey());
        if (!adminUser.getPassword().equals(string)) {
            return ResponseEntity.ok(RunResult.fail("密码错误"));
        }

        return buildResponse(adminUser);
    }

    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, loginServerProperties.getJwtKey());
            if (adminUserToken == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            AdminUser adminUser = sqlDataHelper.getCacheService().cacheIfPresent(AdminUser.class, adminUserToken.getUserName());
            if (adminUser == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            return buildResponse(adminUser);
        } catch (Exception e) {
            return ResponseEntity.ok(RunResult.fail("token过期"));
        }
    }

}
