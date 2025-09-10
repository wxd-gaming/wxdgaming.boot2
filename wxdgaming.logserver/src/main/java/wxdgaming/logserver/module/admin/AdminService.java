package wxdgaming.logserver.module.admin;

import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Start;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.core.util.SignUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.logserver.LogServerProperties;
import wxdgaming.logserver.bean.AdminUserToken;
import wxdgaming.logserver.entity.AdminUserEntity;

import java.util.concurrent.TimeUnit;

/**
 * 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-10 16:29
 **/
@Slf4j
@Service
public class AdminService extends HoldApplicationContext {

    private final LogServerProperties logServerProperties;
    private final SqlDataHelper sqlDataHelper;

    public AdminService(LogServerProperties logServerProperties, PgsqlDataHelper pgsqlDataHelper) {
        this.logServerProperties = logServerProperties;
        this.sqlDataHelper = pgsqlDataHelper;
    }

    @Start
    public void start() {
        String adminName = logServerProperties.getAdminName();
        AdminUserEntity admin = this.sqlDataHelper.getCacheService().cacheIfPresent(AdminUserEntity.class, adminName);
        if (admin == null) {
            AdminUserEntity adminUserEntity = new AdminUserEntity();
            adminUserEntity.setUserName(adminName);
            adminUserEntity.setPassword(SignUtil.signByJsonKey(logServerProperties.getAdminPwd(), logServerProperties.getAdminKey()));
            this.sqlDataHelper.insert(adminUserEntity);
        }
    }

    ResponseEntity<RunResult> buildResponse(AdminUserEntity adminUserEntity) {
        AdminUserToken adminUserToken = new AdminUserToken();
        adminUserToken.setUserName(adminUserEntity.getUserName());

        String jsonToken = JsonTokenBuilder.of(logServerProperties.getAdminKey(), TimeUnit.DAYS, 7)
                .put("user", adminUserToken)
                .compact();

        ResponseCookie cookie = ResponseCookie.from(HttpHeaderNames.AUTHORIZATION.toString(), jsonToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(TimeUnit.DAYS.toSeconds(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(RunResult.ok().data(jsonToken));
    }

    public ResponseEntity<RunResult> login(String userName, String password) {
        AdminUserEntity adminUserEntity = sqlDataHelper.getCacheService().cacheIfPresent(AdminUserEntity.class, userName);
        if (adminUserEntity == null) {
            return ResponseEntity.ok(RunResult.fail("用户不存在"));
        }

        String string = SignUtil.signByJsonKey(password, logServerProperties.getAdminKey());
        if (!adminUserEntity.getPassword().equals(string)) {
            return ResponseEntity.ok(RunResult.fail("密码错误"));
        }

        return buildResponse(adminUserEntity);
    }

    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, logServerProperties.getAdminKey());
            if (adminUserToken == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            AdminUserEntity adminUserEntity = sqlDataHelper.getCacheService().cacheIfPresent(AdminUserEntity.class, adminUserToken.getUserName());
            if (adminUserEntity == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            return buildResponse(adminUserEntity);
        } catch (Exception e) {
            return ResponseEntity.ok(RunResult.fail("token过期"));
        }
    }

}
