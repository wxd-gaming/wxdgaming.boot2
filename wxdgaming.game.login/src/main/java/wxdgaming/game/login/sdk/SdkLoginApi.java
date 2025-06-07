package wxdgaming.game.login.sdk;

import com.google.inject.Inject;
import io.jsonwebtoken.JwtBuilder;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.JwtUtils;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.service.LoginService;

import java.util.concurrent.TimeUnit;

/**
 * 渠道参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:22
 **/
public abstract class SdkLoginApi {

    @Inject protected LoginService loginService;
    @SuppressWarnings("rawtypes")
    @Inject protected SqlDataHelper sqlDataHelper;

    /** 平台 */
    public abstract int platform();

    public abstract RunResult login(HttpContext context);

    public RunResult buildResult(UserData userData) {
        JwtBuilder jwtBuilder = JwtUtils.createJwtBuilder(TimeUnit.MINUTES.toMillis(5));
        jwtBuilder.claim("platform", userData.getPlatform());
        jwtBuilder.claim("account", userData.getAccount());
        jwtBuilder.claim("platformUserId", userData.getPlatformUserId());
        String token = jwtBuilder.compact();
        return RunResult.ok()
                .fluentPut("userId", userData.getPlatformUserId())
                .fluentPut("token", token);
    }

}
