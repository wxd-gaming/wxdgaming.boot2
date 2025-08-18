package wxdgaming.game.server.script.bag.use.impl;

import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.UseItemAction;

/**
 * 默认使用
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 17:29
 **/
public class DefaultUseItemActionImpl extends UseItemAction {

    @Override public ItemTypeConst type() {
        return super.type();
    }

    @Override public boolean canUse(Player player, BagChangesContext bagChangesContext, Item item) {
        return super.canUse(player, bagChangesContext, item);
    }

    @Override public void doUse(Player player, BagChangesContext bagChangesContext, Item item) {
        super.doUse(player, bagChangesContext, item);
    }

}
