package wxdgaming.game.login.service.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.basic.login.AppPlatformParams;
import wxdgaming.game.login.sdk.AbstractSdkLoginApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:41
 **/
@Slf4j
@RestController
@RequestMapping(value = "/login")
public class LoginController extends HoldApplicationContext {

    Map<AppPlatformParams.Platform, AbstractSdkLoginApi> sdkMap = new HashMap<>();

    @Init
    public void init() {

        HashMap<AppPlatformParams.Platform, AbstractSdkLoginApi> map = new HashMap<>();

        applicationContextProvider.classWithSuper(AbstractSdkLoginApi.class)
                .forEach(sdkLoginApi -> {
                    AbstractSdkLoginApi oldPut = map.put(sdkLoginApi.platform(), sdkLoginApi);
                    AssertUtil.assertTrue(oldPut == null, "重复注册类型：" + sdkLoginApi.platform());
                    log.info("register sdk login api: {}", sdkLoginApi.platform());
                });

        sdkMap = Collections.unmodifiableMap(map);
    }

    @RequestMapping(value = "/check")
    public RunResult check(HttpServletRequest context, @RequestParam(value = "appId") int appId) throws Exception {
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

    @RequestMapping(value = "/test/{id}/sdk")
    public RunResult checkSdk(HttpServletRequest context, @PathVariable("id") int id) {
        return RunResult.fail(String.valueOf(id));
    }

    @RequestMapping(value = "/test/{id}/v1")
    public RunResult testV1(HttpServletRequest context, @RequestBody() String body) {
        log.info("body: {}", body);
        return RunResult.ok().fluentPut("data", body);
    }

}
