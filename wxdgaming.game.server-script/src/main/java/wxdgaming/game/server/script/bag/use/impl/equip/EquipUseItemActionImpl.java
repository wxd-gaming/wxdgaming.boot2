package wxdgaming.game.server.script.bag.use.impl.equip;

import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.Equipment;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.equip.EquipPack;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.use.AbstractUseItemAction;

/**
 * 血量增加
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-02 11:12
 */
@Component
public class EquipUseItemActionImpl extends AbstractUseItemAction {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.EquipType;
    }

    @Override public boolean canUse(Player player, BagChangesContext bagChangesContext, Item item) {
        if (!(item instanceof Equipment)) {
            return false;
        }
        QItem qItem = item.qItem();
        /*使用等级*/
        return qItem.getLv() <= player.getLevel();
    }

    @Override public void doUse(Player player, BagChangesContext bagChangesContext, Item item) {
        QItem qItem = item.qItem();
        EquipPack equipPack = player.getEquipPack();

    }

}
