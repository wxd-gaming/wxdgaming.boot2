package wxdgaming.game.test.script.goods.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.goods.ItemTypeConst;
import wxdgaming.game.test.bean.role.Player;

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

    @Override public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return player.getExp();
    }

    @Override public boolean gain(Player player, ItemBag itemBag, long serialNumber, Item newItem, Object... args) {
        long count = newItem.getCount();
        playerService.addExp(player, count, args);
        return true;
    }

}
