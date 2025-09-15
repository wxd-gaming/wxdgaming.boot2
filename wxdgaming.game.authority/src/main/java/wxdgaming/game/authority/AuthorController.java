package wxdgaming.game.authority;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;

/**
 * 验证接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-15 09:23
 **/
@Slf4j
@RestController
@RequestMapping("/author")
public class AuthorController implements InitPrint {

    @Value("${boot.adminKey}")
    private String adminKey;

    public AuthorController() {
    }

    @RequestMapping("/check")
    public ResponseEntity<RunResult> check(CacheHttpServletRequest request) {
        try {
            AdminUserToken adminUserToken = AdminUserToken.parse(request, adminKey);
            if (adminUserToken == null) {
                return ResponseEntity.ok(RunResult.fail("token过期"));
            }
            return buildResponse(adminUserToken);
        } catch (Exception e) {
            return ResponseEntity.ok(RunResult.fail("token过期"));
        }
    }

    public ResponseEntity<RunResult> buildResponse(AdminUserToken adminUserToken) {
        String jsonToken = JsonTokenBuilder.of(adminKey, adminUserToken.getExpireTime())
                .put("user", adminUserToken)
                .compact();

        ResponseCookie cookie = ResponseCookie.from(AdminUserToken.authorization, jsonToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(RunResult.ok());
    }

}
