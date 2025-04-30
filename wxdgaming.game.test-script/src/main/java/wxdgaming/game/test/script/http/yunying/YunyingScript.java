package wxdgaming.game.test.script.http.yunying;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.net.ann.HttpRequest;
import wxdgaming.boot2.starter.net.ann.RequestMapping;
import wxdgaming.boot2.starter.net.server.http.HttpContext;
import wxdgaming.game.test.module.data.GlobalDataService;

/**
 * 运营接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:28
 **/
@Slf4j
@Singleton
@RequestMapping(path = "yunying")
public class YunyingScript extends HoldRunApplication {

    private final GlobalDataService globalDataService;

    @Inject
    public YunyingScript(GlobalDataService globalDataService) {
        this.globalDataService = globalDataService;
    }

    @HttpRequest
    public Object cdKeyList(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object mail(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object banLogin(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object banChat(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object queryRole(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object addPlayerGm(HttpContext httpContext) {

        return RunResult.ok();
    }

    @HttpRequest
    public Object addAccountGm(HttpContext httpContext) {

        return RunResult.ok();
    }

}
