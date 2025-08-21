package wxdgaming.game.login.sdk;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.token.JsonTokenBuilder;
import wxdgaming.boot2.core.util.SingletonLockUtil;
import wxdgaming.boot2.starter.batis.sql.SqlDataHelper;
import wxdgaming.game.basic.login.AppPlatformParams;
import wxdgaming.game.basic.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.basic.slog.SlogService;
import wxdgaming.game.login.LoginServerProperties;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.login.service.LoginService;
import wxdgaming.game.login.slog.AccountLoginLog;
import wxdgaming.game.login.slog.AccountRegisterLog;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 渠道参数
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:22
 **/
public abstract class AbstractSdkLoginApi {

    @Autowired protected LoginService loginService;
    @Autowired protected SqlDataHelper sqlDataHelper;
    @Autowired protected LoginServerProperties loginServerProperties;
    @Autowired protected InnerService innerService;
    @Autowired protected SlogService slogService;

    /** 平台 */
    public abstract AppPlatformParams.Platform platform();

    public abstract RunResult login(HttpServletRequest context, AppPlatformParams appPlatformParams) throws Exception;

    protected UserData createUserData(String ip, String account, AppPlatformParams appPlatformParams, String platformUserId) {
        UserData userData = new UserData();
        userData.setAccount(account);
        userData.setAppId(appPlatformParams.getAppId());
        userData.setPlatform(appPlatformParams.getPlatform().name());
        userData.setPlatformUserId(platformUserId);
        userData.setCreateTime(System.currentTimeMillis());

        AccountRegisterLog accountLoginLog = new AccountRegisterLog(
                userData.getAccount(),
                userData.getPlatform(),
                userData.getPlatformChannelId(),
                ip
        );
        slogService.addLog(accountLoginLog);

        return userData;
    }

    public UserData getUserData(String account, Supplier<UserData> supplier) {
        SingletonLockUtil.lock(account);
        try {
            UserData userData = loginService.userData(account);
            if (userData == null) {
                userData = supplier.get();
                sqlDataHelper.getCacheService().cache(UserData.class).put(userData.getAccount(), userData);
            }
            return userData;
        } finally {
            SingletonLockUtil.unlock(account);
        }
    }

    /** 登录成功 */
    public RunResult loginSuccess(UserData userData, String loginIp) {
        JsonTokenBuilder jwtBuilder = JsonTokenBuilder.of(loginServerProperties.getJwtKey(), TimeUnit.MINUTES, 5);
        jwtBuilder.put("appId", userData.getAppId());
        jwtBuilder.put("platform", userData.getPlatform());
        jwtBuilder.put("account", userData.getAccount());
        jwtBuilder.put("platformUserId", userData.getPlatformUserId());
        String token = jwtBuilder.compact();
        InnerServerInfoBean gateway = innerService.idleGateway();
        if (gateway != null) {
            gateway.setOnlineSize(gateway.getOnlineSize() + 1);
        }
        AccountLoginLog accountLoginLog = new AccountLoginLog(
                userData.getAccount(),
                userData.getPlatform(),
                userData.getPlatformChannelId(),
                loginIp
        );
        slogService.addLog(accountLoginLog);

        return RunResult.ok()
                .fluentPut("userId", userData.getPlatformUserId())
                .fluentPut("token", token)
                .fluentPut("host", gateway == null ? "" : gateway.getHost())
                .fluentPut("port", gateway == null ? 0 : gateway.getPort());
    }

}
