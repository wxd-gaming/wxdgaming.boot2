package wxdgaming.game.login.login.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.ann.InitEvent;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.game.common.bean.login.AppPlatformParams;
import wxdgaming.game.login.login.sdk.AbstractSdkLoginApi;

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

    @EventListener
    public void init(InitEvent initEvent) {
        sdkMap = applicationContextProvider.toMap(AbstractSdkLoginApi.class, AbstractSdkLoginApi::platform);
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
