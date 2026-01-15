package wxdgaming.game.center.modules.recharge.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.game.center.entity.order.OrderBean;

/**
 * 订单号
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 15:30
 **/
@Slf4j
@Controller
@RequestMapping("/order")
public class OrderController implements InitPrint {

    public OrderController() {
    }

    @RequestMapping("/create")
    @ResponseBody
    public Object create(@RequestBody OrderBean orderBean) {
        return null;
    }

}
