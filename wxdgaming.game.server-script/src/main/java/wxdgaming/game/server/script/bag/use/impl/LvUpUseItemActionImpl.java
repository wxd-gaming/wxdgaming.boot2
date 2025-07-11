package wxdgaming.game.server.script.bag.use.impl;

import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.BagChangesProcess;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.UseItemAction;

/**
 * 等级丹
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-02 11:12
 */
public class LvUpUseItemActionImpl extends UseItemAction {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.HPADD;
    }

    @Override public boolean canUse(Player player, BagChangesProcess bagChangesProcess, Item item) {
        return player.getHp() < player.maxHp();
    }

    @Override public void doUse(Player player, BagChangesProcess bagChangesProcess, Item item) {
        QItem qItem = item.qItem();
        playerService.addLevel(player, qItem.getParam1(), bagChangesProcess.getReasonArgs());
    }

}
