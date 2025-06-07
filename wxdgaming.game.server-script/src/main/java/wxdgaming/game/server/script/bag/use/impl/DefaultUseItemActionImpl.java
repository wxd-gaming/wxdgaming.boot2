package wxdgaming.game.server.script.bag.use.impl;

import wxdgaming.game.server.bean.bag.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.UseItemAction;

/**
 * 默认使用
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 17:29
 **/
public class DefaultUseItemActionImpl extends UseItemAction {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

    @Override public boolean canUse(Player player, Item item) {
        return true;
    }

    @Override public void doUse(Player player, Item item) {

    }
}
