package wxdgaming.game.login.service.api;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.ann.Param;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.boot2.starter.net.ann.HttpPath;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.login.AppPlatformParams;
import wxdgaming.game.login.sdk.AbstractSdkLoginApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:41
 **/
@Slf4j
@Singleton
@RequestMapping(path = "/login")
public class LoginController extends HoldRunApplication {

    Map<AppPlatformParams.Platform, AbstractSdkLoginApi> sdkMap = new HashMap<>();

    @Init
    public void init() {

        HashMap<AppPlatformParams.Platform, AbstractSdkLoginApi> map = new HashMap<>();

        runApplication.classWithSuper(AbstractSdkLoginApi.class)
                .forEach(sdkLoginApi -> {
                    AbstractSdkLoginApi oldPut = map.put(sdkLoginApi.platform(), sdkLoginApi);
                    AssertUtil.assertTrue(oldPut == null, "重复注册类型：" + sdkLoginApi.platform());
                    log.info("register sdk login api: {}", sdkLoginApi.platform());
                });

        sdkMap = Collections.unmodifiableMap(map);
    }

    @HttpRequest
    public RunResult check(HttpContext context, @Param(path = "appId") int appId) {
        AppPlatformParams appPlatformParams = AppPlatformParams.getAppPlatformParams(appId);
        if (appPlatformParams == null) {
            return RunResult.fail("not support appId: " + appId + " not exist");
        }
        AppPlatformParams.Platform platform = appPlatformParams.getPlatform();
        AbstractSdkLoginApi sdkLoginApi = sdkMap.get(platform);
        if (sdkLoginApi == null) {
            return RunResult.fail("not support platform: " + platform);
        }
        return sdkLoginApi.login(context, appPlatformParams);
    }

    @HttpRequest(path = "test/{id}/sdk")
    public RunResult checkSdk(HttpContext context, @HttpPath("id") int id) {
        return RunResult.fail(String.valueOf(id));
    }

}
