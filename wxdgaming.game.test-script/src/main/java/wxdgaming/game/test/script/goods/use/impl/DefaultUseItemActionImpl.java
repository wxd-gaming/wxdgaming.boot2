package wxdgaming.game.test.script.goods.use.impl;

import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemTypeConst;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.goods.use.UseItemAction;

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
