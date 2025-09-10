package wxdgaming.logserver.module.admin;

import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
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


    ResponseEntity<RunResult> buildResponse(AdminUserEntity adminUserEntity) {
        AdminUserToken adminUserToken = new AdminUserToken();
        adminUserToken.setUserName(adminUserEntity.getUserName());

        String jsonToken = JsonTokenBuilder.of(logServerProperties.getAdminKey(), TimeUnit.DAYS, 7)
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
        AdminUserEntity adminUserEntity = sqlDataHelper.getCacheService().cacheIfPresent(AdminUserEntity.class, userName);
        if (adminUserEntity == null) {
            return ResponseEntity.ok(RunResult.fail("用户不存在"));
        }

        String string = SignUtil.signByJsonKey(password, loginServerProperties.getJwtKey());
        if (!adminUserEntity.getPassword().equals(string)) {
            return ResponseEntity.ok(RunResult.fail("密码错误"));
        }

        return buildResponse(adminUserEntity);
    }

    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, loginServerProperties.getJwtKey());
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
