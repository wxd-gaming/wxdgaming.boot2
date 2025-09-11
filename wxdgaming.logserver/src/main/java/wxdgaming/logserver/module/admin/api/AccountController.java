package wxdgaming.logserver.module.admin.api;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.game.authority.AdminUserToken;
import wxdgaming.logserver.LogServerProperties;

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

    final LogServerProperties logServerProperties;

    public AccountController(LogServerProperties logServerProperties) {
        this.logServerProperties = logServerProperties;
    }

    @RequestMapping("/check")
    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, logServerProperties.getAdminKey());
            if (adminUserToken == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            return buildResponse(adminUserToken);
        } catch (Exception e) {
            return ResponseEntity.ok(RunResult.fail("token过期"));
        }
    }

    public ResponseEntity<RunResult> buildResponse(AdminUserToken adminUserToken) {
        String jsonToken = JsonTokenBuilder.of(logServerProperties.getAdminKey(), adminUserToken.getExpireTime())
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

}
