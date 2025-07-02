package wxdgaming.game.server.script.bag.gain.impl.currency;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.bag.BagChangesProcess;
import wxdgaming.game.server.script.bag.gain.GainScript;

import java.util.List;

/**
 * 货币的获得
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class CurrencyGainScript extends GainScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.CurrencyType;
    }

    @Override public void newItem(List<Item> items, ItemCfg itemCfg) {
        Item item = new Item();
        item.setCfgId(itemCfg.getCfgId());
        item.setCount(itemCfg.getNum());
        items.add(item);
    }

    @Override public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag.getCurrencyMap().getOrDefault(cfgId, 0L);
    }

    @Override public boolean gain(BagChangesProcess bagChangesProcess, Item newItem) {
        int cfgId = newItem.getCfgId();
        long count = newItem.getCount();
        bagChangesProcess.addCurrency(cfgId, count);
        return true;
    }

}
