package wxdgaming.game.login.inner.api;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.format.data.Data2Json;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.login.ban.BanService;
import wxdgaming.game.login.bean.ServerInfoDTO;
import wxdgaming.game.login.bean.ServerMailDTO;
import wxdgaming.game.login.bean.UseGiftCodeDTO;
import wxdgaming.game.login.entity.ServerInfoEntity;
import wxdgaming.game.login.entity.UserData;
import wxdgaming.game.login.giftcode.GiftCodeService;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.login.login.LoginService;
import wxdgaming.game.login.smail.ServerMailService;

import java.util.List;

/**
 * 登录接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:41
 **/
@Slf4j
@RestController
@RequestMapping(value = "/inner/game")
public class InnerGameController extends HoldApplicationContext {

    final InnerService innerService;
    final GiftCodeService giftCodeService;
    final LoginService loginService;
    final ServerMailService serverMailService;
    final BanService banService;

    public InnerGameController(InnerService innerService,
                               GiftCodeService giftCodeService,
                               LoginService loginService,
                               ServerMailService serverMailService,
                               BanService banService) {
        this.innerService = innerService;
        this.giftCodeService = giftCodeService;
        this.loginService = loginService;
        this.serverMailService = serverMailService;
        this.banService = banService;
    }

    @RequestMapping("/banList")
    public RunResult use(CacheHttpServletRequest request) {
        List<String> list = this.banService.getBanEntityDbDataTable().getList().stream().distinct().map(Data2Json::toJSONString).toList();
        return RunResult.ok().data(list);
    }

    @RequestMapping("/giftCode/use")
    public RunResult use(CacheHttpServletRequest request, @RequestBody UseGiftCodeDTO dto) {
        return giftCodeService.use(dto.getGiftCode(), dto.getSid(), dto.getAccount(), dto.getRoleId(), dto.getRoleName());
    }

    @RequestMapping("/serverMail/query")
    public RunResult serverMailDTOLIST(CacheHttpServletRequest request, @RequestBody JSONObject jsonObject) {
        int sid = jsonObject.getIntValue("sid");
        List<ServerMailDTO> serverMailDTOS = serverMailService.serverMailDTOList(sid);
        return RunResult.ok().data(serverMailDTOS);
    }

    @RequestMapping(value = "/lastLogin")
    public RunResult lastLogin(CacheHttpServletRequest request, @RequestBody JSONObject params) {
        String account = params.getString("account");
        if (StringUtils.isBlank(account)) {
            return RunResult.fail("请输入账号");
        }
        Integer sid = params.getInteger("sid");
        if (sid == null) {
            return RunResult.fail("请输入服务器ID");
        }
        String string = params.getString("roleInfo");
        UserData userData = loginService.userData(account);
        if (userData == null) {
            return RunResult.fail("账号异常");
        }
        userData.setLastLoginServerId(sid);
        userData.setLastLoginServerTime(MyClock.nowString());
        userData.getGameRoleMap().put(sid, string);
        return RunResult.ok();
    }

    @RequestMapping(value = "/sync")
    public RunResult registerGame(CacheHttpServletRequest request, @RequestBody ServerInfoDTO serverInfoDTO) {
        int sid = serverInfoDTO.getSid();
        int onlineSize = serverInfoDTO.getOnlineSize();
        ServerInfoEntity entity = innerService.getInnerGameServerInfoMap().get(sid);
        if (entity == null) {
            return RunResult.fail("服务器不存在");
        }
        entity.setHost(SpringUtil.getClientIp(request));
        entity.setInnerHost(SpringUtil.getClientIp(request));
        entity.setPort(serverInfoDTO.getPort());
        entity.setHttpPort(serverInfoDTO.getHttpPort());
        entity.setOnlineSize(onlineSize);
        entity.setLastSyncTime(MyClock.millis());
        innerService.getSqlDataHelper().getDataBatch().save(entity);
        return RunResult.ok();
    }

}
