package wxdgaming.game.test.script.goods.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.goods.gain.GainScript;

/**
 * 公会货币的获得
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class GuildCurrencyGainScript extends GainScript {

    @Override public int type() {
        return 1;
    }

    @Override public int subType() {
        return 1;
    }

    @Override public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag.getCurrencyMap().getOrDefault(cfgId, 0L);
    }

    @Override public boolean gain(Player player, ItemBag itemBag, Item newItem) {
        int cfgId = newItem.getCfgId();
        long count = newItem.getCount();
        itemBag.getCurrencyMap().merge(cfgId, count, Math::addExact);
        return true;
    }

}
