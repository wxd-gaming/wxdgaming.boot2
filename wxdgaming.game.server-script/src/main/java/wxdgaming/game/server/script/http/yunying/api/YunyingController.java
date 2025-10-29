package wxdgaming.game.server.script.http.yunying.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.json.FastJsonUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.common.bean.ban.BanType;
import wxdgaming.game.common.bean.ban.BanVO;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.server.entity.role.PlayerSnap;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.data.GlobalDbDataCenterService;

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

    final GlobalDataService globalDataService;
    final DataCenterService dataCenterService;
    final GlobalDbDataCenterService globalDbDataCenterService;

    public YunyingController(GlobalDataService globalDataService, DataCenterService dataCenterService, GlobalDbDataCenterService globalDbDataCenterService) {
        this.globalDataService = globalDataService;
        this.dataCenterService = dataCenterService;
        this.globalDbDataCenterService = globalDbDataCenterService;
    }


    @RequestMapping(value = "/mail")
    public Object mail(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(value = "/kick")
    public Object kick(HttpServletRequest httpContext, @RequestParam("account") String account) {
        if ("ALL".equals(account)) {
            dataCenterService.kickAccountAll();
        } else {
            dataCenterService.kickAccount(account);
        }
        return RunResult.ok();
    }

    @RequestMapping(value = "/ban")
    public Object ban(HttpServletRequest httpContext, @RequestParam("data") String dataJson) {
        BanVO parse = FastJsonUtil.parse(dataJson, BanVO.class);
        globalDataService.editBanVOTable(parse);
        log.info("运营后台 禁止：{}, {}, {}, {}", parse.getBanType(), parse.getKey(), MyClock.formatDate(parse.getExpireTime()), parse.getComment());
        if (parse.getBanType() == BanType.AccountLogin) {
            kick(httpContext, parse.getKey());
        }
        if (parse.getBanType() == BanType.RoleLogin) {
            PlayerSnap playerSnap = this.globalDbDataCenterService.playerSnap(Long.parseLong(parse.getKey()));
            String account = playerSnap.getAccount();
            kick(httpContext, account);
        }
        return RunResult.ok();
    }

    @RequestMapping(value = "/banLogin")
    public Object banLogin(HttpServletRequest httpContext, @RequestParam("account") String account, @RequestParam("banTime") long banTime) {

        return RunResult.ok();
    }

    @RequestMapping(value = "/banChat")
    public Object banChat(HttpServletRequest httpContext, @RequestParam("account") String account, @RequestParam("banTime") long banTime) {

        return RunResult.ok();
    }

    @RequestMapping(value = "/queryRole")
    public Object queryRole(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

}
