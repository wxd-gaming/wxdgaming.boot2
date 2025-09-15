package wxdgaming.game.server.script.http.yunying.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.YunyingData;

/**
 * 运营接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:28
 **/
@Slf4j
@RestController
@RequestMapping(value = "/yunying")
public class YunyingController extends HoldApplicationContext {

    private final GlobalDataService globalDataService;

    public YunyingController(GlobalDataService globalDataService) {
        this.globalDataService = globalDataService;
    }


    @RequestMapping(value = "/mail")
    public Object mail(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(value = "/banLogin")
    public Object banLogin(HttpServletRequest httpContext, @RequestParam("account") String account, @RequestParam("banTime") long banTime) {
        YunyingData yunyingData = globalDataService.get(GlobalDataConst.YUNYINGDATA);
        return RunResult.ok();
    }

    @RequestMapping(value = "/banChat")
    public Object banChat(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(value = "/queryRole")
    public Object queryRole(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

}
