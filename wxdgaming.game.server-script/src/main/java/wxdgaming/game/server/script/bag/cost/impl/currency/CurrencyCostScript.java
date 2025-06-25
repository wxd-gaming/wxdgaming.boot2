package wxdgaming.game.server.script.bag.cost.impl.currency;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.bag.ItemGrid;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.bag.BagChangesEvent;
import wxdgaming.game.server.script.bag.cost.CostScript;

import java.util.HashMap;

/**
 * 货币的扣除
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class CurrencyCostScript extends CostScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.CurrencyType;
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, QItem qItem, long count) {
        int cfgId = qItem.getId();
        bagChangesEvent.subtractCurrency(cfgId, count);
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, ItemGrid itemGrid, long count) {
        cost(player, bagChangesEvent, itemGrid.getItem().qItem(), count);
    }
}
