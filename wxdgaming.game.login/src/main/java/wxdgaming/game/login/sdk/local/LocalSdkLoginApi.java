package wxdgaming.game.login.sdk.local;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.chatset.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
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

        String account = context.getRequest().getReqParams().getString("account");
        String token = context.getRequest().getReqParams().getString("token");
        if (StringUtils.isBlank(token)) {
            return RunResult.fail("token is null");
        }

        if (StringUtils.isBlank(account)) {
            return RunResult.fail("account is null");
        }

        UserData userData = loginService.userData(account);
        if (userData == null) {
            userData = new UserData();
            userData.setAccount(account);
            userData.setToken(token);
            userData.setPlatformUserId(account);
            userData.setPlatform("local");
            userData.setCreateTime(System.currentTimeMillis());
            sqlDataHelper.getCacheService().cache(UserData.class).put(account, userData);
        } else {
            if (!token.equals(userData.getToken())) {
                return RunResult.fail("token error");
            }
        }

        return buildResult(userData);
    }


}
