package wxdgaming.game.login.external.api;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.game.login.inner.InnerService;

import java.util.List;

/**
 * 列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-18 20:12
 **/
@Slf4j
@Controller
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
}
