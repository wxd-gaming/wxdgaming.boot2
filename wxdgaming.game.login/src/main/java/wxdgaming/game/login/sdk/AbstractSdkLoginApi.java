package wxdgaming.game.login.sdk;

import com.google.inject.Inject;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.login.service.LoginService;

import java.util.concurrent.TimeUnit;

/**
 * 渠道参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:22
 **/
public abstract class AbstractSdkLoginApi {

    @Inject protected LoginService loginService;
    @SuppressWarnings("rawtypes")
    @Inject protected SqlDataHelper sqlDataHelper;
    @Inject protected LoginConfig loginConfig;
    @Inject protected InnerService innerService;

    /** 平台 */
    public abstract int platform();

    public abstract RunResult login(HttpContext context);

    public UserData createUserData(String account, String platform, String platformUserId) {
        UserData userData = new UserData();
        userData.setAccount(account);
        userData.setPlatform(platform);
        userData.setPlatformUserId(platformUserId);
        userData.setCreateTime(System.currentTimeMillis());
        sqlDataHelper.getCacheService().cache(UserData.class).put(userData.getAccount(), userData);
        return userData;
    }

    public RunResult buildResult(UserData userData) {
        JsonTokenBuilder jwtBuilder = JsonTokenBuilder.of(loginConfig.getJwtKey(), TimeUnit.MINUTES, 5);
        jwtBuilder.putData("platform", userData.getPlatform());
        jwtBuilder.putData("account", userData.getAccount());
        jwtBuilder.putData("platformUserId", userData.getPlatformUserId());
        String token = jwtBuilder.compact();
        InnerServerInfoBean gateway = innerService.idleGateway();
        if (gateway != null) {
            gateway.setOnlineSize(gateway.getOnlineSize() + 1);
        }
        return RunResult.ok()
                .fluentPut("userId", userData.getPlatformUserId())
                .fluentPut("token", token)
                .fluentPut("host", gateway == null ? "" : gateway.getHost())
                .fluentPut("port", gateway == null ? 0 : gateway.getPort());
    }

}
