package wxdgaming.game.server.script.bag.use.impl;

import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.AbstractUseItemAction;

/**
 * 等级丹
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-02 11:12
 */
@Component
public class LvUpUseItemActionImpl extends AbstractUseItemAction {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.HPADD;
    }

    @Override public boolean canUse(Player player, BagChangesContext bagChangesContext, Item item) {
        return player.getHp() < player.maxHp();
    }

    @Override public void doUse(Player player, BagChangesContext bagChangesContext, Item item) {
        QItem qItem = item.qItem();
        playerService.addLevel(player, qItem.getParam1(), bagChangesContext.getReasonDTO());
    }

}
