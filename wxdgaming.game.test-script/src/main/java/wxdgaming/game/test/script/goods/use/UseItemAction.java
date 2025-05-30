package wxdgaming.game.test.script.goods.use;

import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemTypeConst;
import wxdgaming.game.test.bean.role.Player;

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
