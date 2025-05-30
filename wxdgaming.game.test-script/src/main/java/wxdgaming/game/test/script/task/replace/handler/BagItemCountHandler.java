package wxdgaming.game.test.script.task.replace.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.UpdateType;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.goods.BagService;
import wxdgaming.game.test.script.task.replace.ConditionReplaceValueHandler;

/**
 * 获取背包道具数量
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:55
 **/
@Singleton
public class BagItemCountHandler implements ConditionReplaceValueHandler {

    private final BagService bagService;

    @Inject
    public BagItemCountHandler(BagService bagsModuleScript) {
        this.bagService = bagsModuleScript;
    }

    @Override public Condition condition() {
        return new Condition("bagitem", "0", "0", UpdateType.Replace);
    }

    @Override public long replaceValue(Player player, Condition condition) {
        int itemCfg = Integer.parseInt(condition.getK2().toString());
        return bagService.itemCount(player, 1, itemCfg);
    }

}
