package wxdgaming.game.test.script.task.init.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import wxdgaming.boot2.core.lang.condition.Condition;
import wxdgaming.boot2.core.lang.condition.UpdateType;
import wxdgaming.boot2.starter.batis.sql.pgsql.PgsqlService;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.goods.BagScript;
import wxdgaming.game.test.script.task.init.ConditionInitValueHandler;

/**
 * 获取背包道具数量
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 20:55
 **/
@Singleton
public class BagItemCountHandler implements ConditionInitValueHandler {

    private final PgsqlService psqlService;
    private final BagScript bagsModuleScript;

    @Inject
    public BagItemCountHandler(PgsqlService psqlService, BagScript bagsModuleScript) {
        this.psqlService = psqlService;
        this.bagsModuleScript = bagsModuleScript;
    }

    @Override public Condition condition() {
        return new Condition("bagitem", "0", "0", UpdateType.Replace);
    }

    @Override public long initValue(Player player, Condition condition) {
        BagPack bagPack = psqlService.getCacheService().cache(BagPack.class, player.getUid());

        return player.getLevel();
    }

}
