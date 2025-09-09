package wxdgaming.game.login.login.sdk.local;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.boot2.core.BootstrapProperties;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.game.common.bean.login.AppPlatformParams;
import wxdgaming.game.login.entity.UserData;
import wxdgaming.game.login.login.sdk.AbstractSdkLoginApi;

/**
 * 本地服
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:26
 **/
@Slf4j
@Component
public class LocalSdkLoginApi extends AbstractSdkLoginApi {

    private final BootstrapProperties bootstrapProperties;

    public LocalSdkLoginApi(BootstrapProperties bootstrapProperties) {
        this.bootstrapProperties = bootstrapProperties;
    }

    @Override public AppPlatformParams.Platform platform() {
        return AppPlatformParams.Platform.LOCAL;
    }

    @Override public RunResult login(HttpServletRequest context, AppPlatformParams appPlatformParams) {

        if (!bootstrapProperties.isDebug()) {
            return RunResult.fail("not debug ban login");
        }

        String account = context.getParameter("account");
        String token = context.getParameter("token");

        if (StringUtils.isBlank(token)) {
            return RunResult.fail("token is null");
        }

        if (StringUtils.isBlank(account)) {
            return RunResult.fail("account is null");
        }

        String finalAccount = platform().name() + "-" + account;

        UserData userData = getUserData(finalAccount, () -> {
            UserData ud = createUserData(SpringUtil.getClientIp(context), finalAccount, appPlatformParams, finalAccount);
            ud.setToken(token);
            return ud;
        });

        if (!token.equals(userData.getToken())) {
            return RunResult.fail("token error");
        }

        return loginSuccess(userData, SpringUtil.getClientIp(context));
    }


}
