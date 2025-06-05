package wxdgaming.game.server.script.bag.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
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

    @Override public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag.getCurrencyMap().getOrDefault(cfgId, 0L);
    }

    @Override public boolean gain(Player player, ResUpdateBagInfo resUpdateBagInfo, BagType bagType, ItemBag itemBag, Item newItem, ReasonArgs reasonArgs) {
        int cfgId = newItem.getCfgId();
        long count = newItem.getCount();
        itemBag.getCurrencyMap().merge(cfgId, count, Math::addExact);
        resUpdateBagInfo.getCurrencyMap().put(cfgId, itemBag.getCurrencyMap().get(cfgId));
        return true;
    }

}
