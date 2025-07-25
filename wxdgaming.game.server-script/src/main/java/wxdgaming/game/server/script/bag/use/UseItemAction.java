package wxdgaming.game.server.script.bag.use;

import com.google.inject.Inject;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.bag.BagChangesProcess;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.fight.FightService;
import wxdgaming.game.server.script.role.PlayerService;

/**
 * 使用道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 17:27
 **/
public abstract class UseItemAction extends HoldRunApplication {

    @Inject protected PlayerService playerService;
    @Inject protected FightService fightService;

    public ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

    public boolean canUse(Player player, BagChangesProcess bagChangesProcess, Item item) {
        return false;
    }

    public void doUse(Player player, BagChangesProcess bagChangesProcess, Item item) {
        throw new RuntimeException("Not Implement");
    }

}
