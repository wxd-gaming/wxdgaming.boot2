package wxdgaming.game.server.script.goods.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.goods.gain.GainScript;

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

    @Override public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag.getCurrencyMap().getOrDefault(cfgId, 0L);
    }

    @Override public boolean gain(Player player, ItemBag itemBag, long serialNumber, Item newItem, Object... args) {
        int cfgId = newItem.getCfgId();
        long count = newItem.getCount();
        itemBag.getCurrencyMap().merge(cfgId, count, Math::addExact);
        return true;
    }

}
