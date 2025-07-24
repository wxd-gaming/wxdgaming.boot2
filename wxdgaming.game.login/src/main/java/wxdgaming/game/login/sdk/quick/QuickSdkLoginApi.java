package wxdgaming.game.login.sdk.quick;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.httpclient5.HttpRequestPost;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.login.AppPlatformParams;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.sdk.AbstractSdkLoginApi;

/**
 * Quick
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-26 10:12
 */
@Slf4j
@Singleton
public class QuickSdkLoginApi extends AbstractSdkLoginApi {

    private static final String APP_LOGIN_URL = "http://checkuser.quickapi.net/v2/checkUserInfo";
    private static final String APP_LOGIN_URL_DATA_FORMAT = "token=%s&uid=%s&product_code=%s";

    @Override public AppPlatformParams.Platform platform() {
        return AppPlatformParams.Platform.QUICK;
    }

    @Override public RunResult login(HttpContext context, AppPlatformParams appPlatformParams) {
        JSONObject reqParams = context.getRequest().getReqParams();
        String user_id = reqParams.getString("userId");
        String token = reqParams.getString("token");
        String channelId = reqParams.getString("channelId");
        if (StringUtils.isBlank(channelId)) {
            return RunResult.fail("登陆失败 channelId null");
        }
        if (checkLogin(appPlatformParams, token, user_id)) {
            /*防止串号*/
            String finalAccount = appPlatformParams.getAppId() + "_" + channelId + "_" + user_id;

            UserData userData = getUserData(finalAccount, () -> {
                UserData ud = createUserData(finalAccount, appPlatformParams, user_id);
                ud.setPlatformChannelId(channelId);
                ud.setToken("*");
                return ud;
            });

            return buildResult(userData);

        }
        return RunResult.fail("登陆失败 token error");
    }

    public boolean checkLogin(AppPlatformParams appPlatformParams, String token, String userId) {
        try {
            String urlData = String.format(APP_LOGIN_URL_DATA_FORMAT, token, userId, appPlatformParams.getLoginKey());
            if (log.isDebugEnabled()) {
                log.debug("{}：post url：{}?{}", appPlatformParams, APP_LOGIN_URL, urlData);
            }
            String URLGetStr = HttpRequestPost.of(APP_LOGIN_URL, urlData).execute().bodyString();
            if (log.isDebugEnabled()) {
                log.debug("{}：check login ret：{}", appPlatformParams, URLGetStr);
            }
            if ("1".equalsIgnoreCase(URLGetStr)) {
                return true;
            } else {
                log.error("{}：请求登录 失败 message:{}", appPlatformParams, URLGetStr);
            }
        } catch (Exception e) {
            log.error("{}：请求登录服务器异常", appPlatformParams, e);
        }
        return false;
    }

}
