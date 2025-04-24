package wxdgaming.game.test.script.goods.gain;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.core.ann.Init;
import wxdgaming.game.test.bean.goods.Item;
import wxdgaming.game.test.bean.goods.ItemBag;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.module.data.DataCenterService;

import java.util.List;

/**
 * 获得道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 19:13
 **/
@Slf4j
@Singleton
public class GainScript extends HoldRunApplication {

    DataCenterService dataCenterService;

    @Init
    public void init(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    public int type() {
        return 0;
    }

    public int subType() {
        return 0;
    }

    /** 创建道具 */
    public void newItem(List<Item> items, ItemCfg itemCfg) {
        long count = itemCfg.getCount();
        do {
            Item item = new Item();
            item.setUid(dataCenterService.getItemHexid().newId());
            item.setCfgId(itemCfg.getCfgId());
            if (count > 99) {
                item.setCount(99);
                count -= 99;
            } else {
                item.setCount(count);
                count = 0;
            }
            item.setBind(itemCfg.isBind());
            item.setExpirationTime(itemCfg.getExpirationTime());
            items.add(item);
        } while (count > 0);
    }

    /** 查询指定道具背包数量 */
    public long gainCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag
                .getItems()
                .stream()
                .filter(item -> item.getCfgId() == cfgId)
                .mapToLong(Item::getCount)
                .sum();
    }

    /** 将道具添加进入背包 */
    public boolean gain(Player player, ItemBag itemBag, Item newItem) {
        long count = newItem.getCount();
        for (Item value : itemBag.getItems()) {
            /* TODO 叠加 */
            if (value.getCfgId() == newItem.getCfgId()
                && value.isBind() == newItem.isBind()/* TODO 绑定状态一致 */
                && value.getExpirationTime() == newItem.getExpirationTime()/* TODO 过期时间一致 */) {
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
