package wxdgaming.game.server.script.bag.use.impl;

import org.springframework.stereotype.Component;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.AbstractUseItemAction;

/**
 * 等级丹
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-02 11:12
 */
@Component
public class ExpUseItemActionImpl extends AbstractUseItemAction {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.Exp;
    }

    @Override public boolean canUse(Player player, BagChangesContext bagChangesContext, Item item) {
        return player.getExp() < Integer.MAX_VALUE;
    }

    @Override public void doUse(Player player, BagChangesContext bagChangesContext, Item item) {
        QItem qItem = item.qItem();
        playerService.addExp(player, qItem.getParam1(), bagChangesContext.getReasonDTO());
    }

}
