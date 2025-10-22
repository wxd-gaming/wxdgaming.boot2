package wxdgaming.game.server.script.recharge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.boot2.core.format.string.String2IntList;
import wxdgaming.boot2.core.util.RandomUtils;
import wxdgaming.game.common.slog.SlogService;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnHeartMinute;
import wxdgaming.game.server.script.recharge.slog.RoleRechargeSlog;

import java.util.List;

/**
 * 充值服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-30 19:28
 **/
@Slf4j
@Service
public class RechargeService extends HoldApplicationContext {

    List<Integer> rechargeList = String2IntList.parse.apply("600,1200,3000,6800,12800,64800");
    final SlogService slogService;

    public RechargeService(SlogService slogService) {
        this.slogService = slogService;
    }

    @OnHeartMinute
    public void onHeartMinute(Player player, int minute) {
        log.info("开始执行充值服务");
        if (RandomUtils.randomBoolean(2000)) {
            int amount = RandomUtils.randomItem(rechargeList);
            RoleRechargeSlog roleRechargeSlog = new RoleRechargeSlog(player);
            roleRechargeSlog.setAmount(amount);
            roleRechargeSlog.setCpOrderId(String.valueOf(System.nanoTime()));
            roleRechargeSlog.setSpOrderId(String.valueOf(System.nanoTime()));
            roleRechargeSlog.setProductId(amount);
            roleRechargeSlog.setProductName(amount + "元");
            roleRechargeSlog.setComment("测试充值");
            slogService.pushLog(roleRechargeSlog);
        }
    }

}
