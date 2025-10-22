package wxdgaming.game.login.admin;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.event.StartEvent;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.game.authority.SignUtil;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.entity.AdminUserEntity;

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

    private final LoginServerProperties loginServerProperties;
    private final SqlDataHelper sqlDataHelper;

    public AdminService(LoginServerProperties loginServerProperties, PgsqlDataHelper pgsqlDataHelper) {
        this.loginServerProperties = loginServerProperties;
        this.sqlDataHelper = pgsqlDataHelper;
    }

    @EventListener
    public void start(StartEvent event) {
        String adminName = loginServerProperties.getAdminName();
        AdminUserEntity admin = findByName(adminName);
        if (admin == null) {
            add(true, loginServerProperties.getAdminName(), loginServerProperties.getAdminPwd(), "13000000000");
        }
    }

    public AdminUserEntity add(boolean admin, String userName, String password, String phone) {
        AdminUserEntity adminUserEntity = new AdminUserEntity();
        adminUserEntity.setUid(userName.toUpperCase());
        adminUserEntity.setUserName(userName);
        save(adminUserEntity, admin, password, phone);
        return adminUserEntity;
    }

    public void save(AdminUserEntity adminUserEntity, boolean admin, String password, String phone) {
        setPassword(adminUserEntity, password);
        adminUserEntity.setAdmin(admin);
        adminUserEntity.setPhone(phone);
        this.sqlDataHelper.save(adminUserEntity);
    }

    public void setPassword(AdminUserEntity adminUserEntity, String password) {
        adminUserEntity.setPassword(SignUtil.signByJsonKey(password, loginServerProperties.getAdminKey()));
    }

    public void delete(String userName) {
        this.sqlDataHelper.deleteByKey(AdminUserEntity.class, userName.toUpperCase());
    }

    public ResponseEntity<RunResult> buildResponse(AdminUserEntity adminUser) {
        AdminUserToken adminUserToken = new AdminUserToken();
        adminUserToken.setAdmin(adminUser.isAdmin());
        adminUserToken.setUserName(adminUser.getUserName());
        adminUserToken.setLv(adminUser.getLv());
        adminUserToken.setLoginCount(adminUser.getLoginCount());
        adminUserToken.setPhone(adminUser.getPhone());
        adminUserToken.setRoutes(adminUser.getRoutes());
        adminUserToken.setExpireTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(12));
        return buildResponse(adminUserToken);
    }

    public ResponseEntity<RunResult> buildResponse(AdminUserToken adminUserToken) {
        String jsonToken = JsonTokenBuilder.of(loginServerProperties.getAdminKey(), adminUserToken.getExpireTime())
                .put("user", adminUserToken)
                .compact();

        ResponseCookie cookie = ResponseCookie.from(HttpHeaderNames.AUTHORIZATION.toString(), jsonToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(adminUserToken.getExpireTime() - System.currentTimeMillis())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(RunResult.ok().data(jsonToken));
    }

    public AdminUserEntity findByName(String userName) {
        return sqlDataHelper.getCacheService().cacheIfPresent(AdminUserEntity.class, userName.toUpperCase());
    }

    public ResponseEntity<RunResult> login(String userName, String password) {
        AdminUserEntity adminUserEntity = findByName(userName);
        if (adminUserEntity == null) {
            return ResponseEntity.ok(RunResult.fail("用户不存在"));
        }

        String string = SignUtil.signByJsonKey(password, loginServerProperties.getAdminKey());
        if (!adminUserEntity.getPassword().equals(string)) {
            return ResponseEntity.ok(RunResult.fail("密码错误"));
        }
        adminUserEntity.setLoginCount(adminUserEntity.getLoginCount() + 1);
        return buildResponse(adminUserEntity);
    }

    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, loginServerProperties.getAdminKey());
            if (adminUserToken == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            AdminUserEntity adminUserEntity = findByName(adminUserToken.getUserName());
            if (adminUserEntity == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            return buildResponse(adminUserEntity);
        } catch (Exception e) {
            return ResponseEntity.ok(RunResult.fail("token过期"));
        }
    }

}
