package wxdgaming.game.server.script.bag.gain.impl.currency;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.bag.goods.Item;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.bag.BagChangesEvent;

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

    @Override public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return super.getCount(player, itemBag, cfgId);
    }

    @Override public boolean gain(BagChangesEvent bagChangesEvent, Item newItem) {
        return super.gain(bagChangesEvent, newItem);
    }
}
