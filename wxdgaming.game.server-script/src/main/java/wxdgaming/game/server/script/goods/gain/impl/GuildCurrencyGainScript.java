package wxdgaming.game.server.script.goods.gain.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.core.Reason;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;

/**
 * 公会货币的获得
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class GuildCurrencyGainScript extends CurrencyGainScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.GuildCurrencyType;
    }

    @Override public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return super.gainCount(player, itemBag, cfgId);
    }

    @Override public boolean gain(Player player, ItemBag itemBag, long serialNumber, Reason reason, Item newItem, Object... args) {
        return super.gain(player, itemBag, serialNumber, reason, newItem);
    }
}
