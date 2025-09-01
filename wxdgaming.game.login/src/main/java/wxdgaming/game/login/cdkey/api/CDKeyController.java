package wxdgaming.game.login.cdkey.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.boot2.core.CacheHttpServletRequest;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.game.login.cdkey.CDKeyService;

/**
 * 激活码接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-01 10:57
 **/
@Slf4j
@RestController
@RequestMapping("/cdkey")
public class CDKeyController implements InitPrint {

    final CDKeyService cdKeyService;

    public CDKeyController(CDKeyService cdKeyService) {
        this.cdKeyService = cdKeyService;
    }

    @RequestMapping("/use")
    public RunResult use(CacheHttpServletRequest request,
                         @RequestParam("sid") int sid,
                         @RequestParam("account") String account,
                         @RequestParam("rid") long rid) {

        return cdKeyService.use(request.getParameter("key"), sid, account, rid);
    }

}
