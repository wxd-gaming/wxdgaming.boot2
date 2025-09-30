package wxdgaming.game.server.script.http.recharge;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.boot2.core.InitPrint;

/**
 * 充值回调
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-30 19:29
 **/
@Slf4j
@Controller
@RequestMapping("/recharge")
public class RechargeHttpController implements InitPrint {

    public RechargeHttpController() {
    }

    @RequestMapping("/call")
    public void call(HttpServletRequest request) {
        log.info("call {}", request.getParameterMap());
    }

}
