package wxdgaming.game.center.modules.recharge.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
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

    private final RocksDBHelper rocksDBHelper;

    @Autowired
    public OrderController(RocksDBHelper rocksDBHelper) {
        this.rocksDBHelper = rocksDBHelper;
    }

    @RequestMapping("/create")
    @ResponseBody
    public Object create(@RequestBody OrderBean orderBean) {
        log.info("创建订单: {}", orderBean);
        String orderId = orderBean.getOrderId();
        if (rocksDBHelper.exits(orderId)) {
            return RunResult.fail("订单号已存在");
        }
        rocksDBHelper.put(orderId, orderBean);
        return RunResult.ok();
    }

}
