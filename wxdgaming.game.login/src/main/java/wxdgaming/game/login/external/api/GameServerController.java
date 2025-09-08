package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.core.timer.MyClock;
import wxdgaming.game.login.inner.InnerService;

import java.util.List;

/**
 * 对外接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-03 09:34
 **/
@Slf4j
@RestController
@RequestMapping("/gameServer")
public class GameServerController implements InitPrint {

    final InnerService innerService;

    public GameServerController(InnerService innerService) {
        this.innerService = innerService;
    }

    @RequestMapping(value = "/list")
    public RunResult list(HttpServletRequest context) {
        List<JSONObject> list = innerService.gameServerList();
        return RunResult.ok().data(list);
    }

    @RequestMapping(value = "/queryList")
    public RunResult queryGameServerList(HttpServletRequest context,
                                         @RequestParam("pageIndex") int pageIndex,
                                         @RequestParam("pageSize") int pageSize,
                                         @RequestParam("where") String where,
                                         @RequestParam("order") String orderJson) {

        if (pageIndex < 1) pageIndex = 1;
        if (pageSize < 10) pageSize = 10;

        int skip = (pageIndex - 1) * pageSize;

        List<JSONObject> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .map(ObjectBase::toJSONObject)
                .skip(skip)
                .limit(pageSize)
                .peek(jsonObject -> jsonObject.put("lastSyncTime", MyClock.formatDate(jsonObject.getLongValue("lastSyncTime"))))
                .toList();
        return RunResult.ok().data(list);
    }

}
