package wxdgaming.game.server.script.bag.cost.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagChangesEvent;
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

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, QItem qItem, long count, ReasonArgs reasonArgs) {
        int cfgId = qItem.getId();
        BagType bagType = bagChangesEvent.getBagType();
        ItemBag itemBag = bagChangesEvent.getItemBag();
        HashMap<Integer, Long> currencyMap = itemBag.getCurrencyMap();
        long hasNum = currencyMap.getOrDefault(cfgId, 0L);
        if (hasNum < count) {
            throw new IllegalArgumentException("货币不足");
        }
        bagChangesEvent.subtractCurrency(cfgId, count);
        log.info(
                "扣除道具：{}, {}, {}, 变更 {} - {} = {}, {}",
                player, bagType, qItem.getToName(), hasNum, count, currencyMap.get(cfgId), reasonArgs
        );
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, Item item, long count, ReasonArgs reasonArgs) {
        cost(player, bagChangesEvent, item.qItem(), count, reasonArgs);
    }
}
