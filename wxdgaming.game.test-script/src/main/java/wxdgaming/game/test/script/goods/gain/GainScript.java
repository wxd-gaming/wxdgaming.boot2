package wxdgaming.game.test.script.goods.gain;

import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.role.Player;

/**
 * 获得道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 19:13
 **/
public class GainScript extends HoldRunApplication {

    public int type() {
        return 0;
    }

    public int subType() {
        return 0;
    }

    public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag
                .getItems()
                .stream()
                .filter(item -> item.getCfgId() == cfgId)
                .mapToLong(Item::getCount)
                .sum();
    }

    public boolean gain(Player player, ItemBag itemBag, Item newItem) {
        long count = newItem.getCount();
        for (Item value : itemBag.getItems()) {
            /* TODO 叠加 */
            if (value.getCfgId() == newItem.getCfgId()) {
                if (value.getCount() < 99) {
                    if (value.getCount() + count > 99) {
                        count = 99 - value.getCount();
                        value.setCount(99);
                    } else {
                        count = 0;
                        value.setCount(value.getCount() + count);
                    }
                }
                if (count < 1)
                    break;
            }
        }
        /* TODO 叠加过后剩余的 */
        newItem.setCount(count);
        if (count > 0) {
            if (itemBag.isFull()) {
                /* TODO 背包已满 */
                return false;
            }
            itemBag.getItems().add(newItem);
        }
        return true;
    }

}
