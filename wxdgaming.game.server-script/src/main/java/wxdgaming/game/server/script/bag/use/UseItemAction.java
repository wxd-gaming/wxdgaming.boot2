package wxdgaming.game.server.script.bag.use;

import wxdgaming.game.server.bean.bag.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;

/**
 * 使用道具, 获得
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 17:27
 **/
public abstract class UseItemAction {

    public abstract ItemTypeConst type();

    public abstract boolean canUse(Player player, Item item);

    public abstract void doUse(Player player, Item item);

}
