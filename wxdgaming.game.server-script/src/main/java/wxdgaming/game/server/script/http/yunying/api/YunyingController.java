package wxdgaming.game.server.script.http.yunying.api;

import io.netty.channel.ChannelFuture;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.common.global.GlobalDataService;
import wxdgaming.game.message.role.ResKick;
import wxdgaming.game.server.bean.UserMapping;
import wxdgaming.game.server.bean.global.GlobalDataConst;
import wxdgaming.game.server.bean.global.impl.YunyingData;
import wxdgaming.game.server.module.data.DataCenterService;

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

    public YunyingController(GlobalDataService globalDataService, DataCenterService dataCenterService) {
        this.globalDataService = globalDataService;
        this.dataCenterService = dataCenterService;
    }


    @RequestMapping(value = "/mail")
    public Object mail(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    public Object kickAccount(HttpServletRequest httpContext, @RequestParam("account") String account) {
        UserMapping userMapping = dataCenterService.getUserMapping(account);
        if (userMapping == null) {
            return RunResult.fail("用户不存在");
        }
        if (userMapping.getSocketSession() != null) {
            ResKick resKick = new ResKick();
            resKick.setReason("被运营后台强制下线");
            ChannelFuture channelFuture = userMapping.writeAndFlush(resKick);
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("强制下线失败：{}", account, future.cause());
                }
                userMapping.getSocketSession().close("被运营后台强制下线");
            });
        }
        return RunResult.ok();
    }

    @RequestMapping(value = "/banLogin")
    public Object banLogin(HttpServletRequest httpContext, @RequestParam("account") String account, @RequestParam("banTime") long banTime) {
        YunyingData yunyingData = globalDataService.get(GlobalDataConst.YUNYINGDATA);
        if (banTime < System.currentTimeMillis()) {
            yunyingData.getAccountBanLoginTime().remove(account);
        } else {
            yunyingData.getAccountBanLoginTime().put(account, banTime);
            kickAccount(httpContext, account);
        }
        log.info("运营后台 禁止登录：{}, {}", account, MyClock.formatDate(banTime));
        return RunResult.ok();
    }

    @RequestMapping(value = "/banChat")
    public Object banChat(HttpServletRequest httpContext, @RequestParam("account") String account, @RequestParam("banTime") long banTime) {
        YunyingData yunyingData = globalDataService.get(GlobalDataConst.YUNYINGDATA);
        if (banTime < System.currentTimeMillis()) {
            yunyingData.getAccountBanChatTime().remove(account);
        } else {
            yunyingData.getAccountBanChatTime().put(account, banTime);
        }
        log.info("运营后台 禁止聊天：{}, {}", account, MyClock.formatDate(banTime));
        return RunResult.ok();
    }

    @RequestMapping(value = "/queryRole")
    public Object queryRole(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

}
