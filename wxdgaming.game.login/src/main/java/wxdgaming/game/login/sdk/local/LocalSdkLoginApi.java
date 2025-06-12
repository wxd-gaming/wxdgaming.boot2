package wxdgaming.game.login.sdk.local;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.GlobalUtil;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.sdk.AbstractSdkLoginApi;

/**
 * 本地服
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:26
 **/
@Slf4j
@Singleton
public class LocalSdkLoginApi extends AbstractSdkLoginApi {


    @Override public int platform() {
        return 1;
    }

    @Override public RunResult login(HttpContext context) {

        if (!GlobalUtil.DEBUG.get()) {
            return RunResult.fail("not debug ban login");
        }

        String account = context.getRequest().getReqParams().getString("account");
        String token = context.getRequest().getReqParams().getString("token");
        if (StringUtils.isBlank(token)) {
            return RunResult.fail("token is null");
        }

        if (StringUtils.isBlank(account)) {
            return RunResult.fail("account is null");
        }

        account = "local-" + account;

        UserData userData = loginService.userData(account);
        if (userData == null) {
            userData = createUserData(account, "local", account);
            userData.setToken(token);
        } else {
            if (!token.equals(userData.getToken())) {
                return RunResult.fail("token error");
            }
        }

        return buildResult(userData);
    }


}
