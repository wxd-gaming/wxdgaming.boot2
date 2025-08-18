package wxdgaming.game.server.script.task.init.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.ConditionUpdatePolicyConst;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.task.init.ConditionInitValueHandler;

/**
 * 获取背包道具数量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-21 20:55
 **/
@Singleton
public class BagItemCountHandler implements ConditionInitValueHandler {

    private final BagService bagService;

    @Inject
    public BagItemCountHandler(BagService bagsModuleScript) {
        this.bagService = bagsModuleScript;
    }

    @Override public Condition condition() {
        return new Condition("bagitem", ConditionUpdatePolicyConst.Replace, 0);
    }

    @Override public long initValue(Player player, Condition condition) {
        int itemCfg = Integer.parseInt(condition.getK2().toString());
        return bagService.itemCount(player, BagType.Bag, itemCfg);
    }

}
