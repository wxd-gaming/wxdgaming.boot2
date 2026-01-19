package wxdgaming.game.center.modules.recharge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.lang.RunResult;
import wxdgaming.boot2.starter.batis.rocksdb.RocksDBHelper;
import wxdgaming.game.center.entity.order.OrderBean;

/**
 * 充值
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-01-15 15:22
 **/
@Slf4j
@Service
public class RechargeService extends HoldApplicationContext {

    private final RocksDBHelper rocksDBHelper;

    public RechargeService(RocksDBHelper rocksDBHelper) {
        this.rocksDBHelper = rocksDBHelper;
    }

    public boolean addRechargeOrder(OrderBean orderBean) {
        log.info("创建订单: {}", orderBean);
        String orderId = orderBean.getOrderId();
        if (rocksDBHelper.exits(orderId)) {
            return false;
        }
        rocksDBHelper.put(orderId, orderBean);
        return true;
    }

    /** 充值回调 */
    public RunResult callRecharge(String cpOrderId, String spOrderId, String amount) {
        log.info("充值: {}", cpOrderId);

        OrderBean orderBean = rocksDBHelper.getObject(cpOrderId, OrderBean.class);
        if (orderBean == null) {
            return RunResult.fail("订单不存在");
        }

        return RunResult.ok();
    }

}
