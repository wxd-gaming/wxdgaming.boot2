package wxdgaming.game.server.script.goods.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;

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

    @Override public boolean gain(Player player, ItemBag itemBag, Item newItem, ReasonArgs reasonArgs) {
        long count = newItem.getCount();
        playerService.addExp(player, count, reasonArgs);
        return true;
    }

}
