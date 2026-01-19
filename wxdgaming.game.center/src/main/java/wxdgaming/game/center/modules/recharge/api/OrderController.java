package wxdgaming.game.center.modules.recharge.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.game.center.entity.order.OrderBean;
import wxdgaming.game.center.modules.recharge.RechargeService;

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

    private final RechargeService rechargeService;

    @Autowired
    public OrderController(RechargeService rechargeService) {
        this.rechargeService = rechargeService;
    }

    @RequestMapping("/create")
    @ResponseBody
    public Object create(@RequestBody OrderBean orderBean) {
        boolean b = rechargeService.addRechargeOrder(orderBean);
        if (!b) {
            return RunResult.fail("订单已存在");
        }
        return RunResult.ok();
    }


}
