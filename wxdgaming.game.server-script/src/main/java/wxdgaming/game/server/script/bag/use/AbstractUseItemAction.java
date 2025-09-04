package wxdgaming.game.server.script.bag.use;

import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.boot2.core.HoldApplicationContext;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.IBagScript;
import wxdgaming.game.server.script.fight.FightService;
import wxdgaming.game.server.script.role.PlayerService;

/**
 * 使用道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 17:27
 **/
public abstract class AbstractUseItemAction extends HoldApplicationContext implements IBagScript {

    @Autowired protected PlayerService playerService;
    @Autowired protected FightService fightService;

    public boolean canUse(Player player, BagChangesContext bagChangesContext, Item item) {
        return false;
    }

    public void doUse(Player player, BagChangesContext bagChangesContext, Item item) {
        throw new RuntimeException("Not Implement");
    }

}
