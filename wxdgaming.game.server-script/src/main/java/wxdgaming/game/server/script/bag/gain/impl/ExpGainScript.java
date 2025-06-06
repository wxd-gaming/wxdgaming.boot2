package wxdgaming.game.server.script.bag.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagChangesEvent;

/**
 * 货币的获得
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class ExpGainScript extends CurrencyGainScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.EXP;
    }

    @Override public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return player.getExp();
    }

    @Override public boolean gain(BagChangesEvent bagChangesEvent, Item newItem) {
        long count = newItem.getCount();
        playerService.addExp(bagChangesEvent.getPlayer(), count, bagChangesEvent.getReasonArgs());
        return true;
    }

}
