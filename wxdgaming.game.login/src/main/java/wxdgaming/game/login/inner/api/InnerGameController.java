package wxdgaming.game.login.inner.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.SpringUtil;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.login.bean.ServerInfoDTO;
import wxdgaming.game.login.bean.UseCDKeyDTO;
import wxdgaming.game.login.cdkey.CDKeyService;
import wxdgaming.game.login.entity.ServerInfoEntity;
import wxdgaming.game.login.inner.InnerService;

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
    final CDKeyService cdKeyService;

    public InnerGameController(InnerService innerService, CDKeyService cdKeyService) {
        this.innerService = innerService;
        this.cdKeyService = cdKeyService;
    }

    @RequestMapping("/cdkey/use")
    public RunResult use(CacheHttpServletRequest request, @RequestBody UseCDKeyDTO dto) {
        return cdKeyService.use(dto.getCdKey(), dto.getSid(), dto.getAccount(), dto.getRoleId(), dto.getRoleName());
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
