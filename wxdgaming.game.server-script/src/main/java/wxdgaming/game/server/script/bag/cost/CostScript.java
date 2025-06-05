package wxdgaming.game.server.script.bag.cost;

import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.role.Player;

/**
 * 扣除道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 19:14
 **/
public class CostScript {

    public int type() {
        return 0;
    }

    public int subType() {
        return 0;
    }

    public boolean cost(Player player, Item item, long count) {
        return true;
    }

}
