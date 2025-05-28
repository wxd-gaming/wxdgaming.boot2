package wxdgaming.game.server.script.goods.gain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.goods.ItemCfg;
import wxdgaming.game.server.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.goods.BagService;
import wxdgaming.game.server.script.role.PlayerService;

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

    @Inject protected DataCenterService dataCenterService;
    @Inject protected PlayerService playerService;
    @Inject protected BagService bagService;


    public ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

    /** 创建道具 */
    public void newItem(List<Item> items, ItemCfg itemCfg) {
        long count = itemCfg.getNum();
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
    public boolean gain(Player player, ItemBag itemBag, long serialNumber, Item newItem, Object... args) {
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
            if (itemBag.checkFull()) {
                /* TODO 背包已满 */
                return false;
            }
            itemBag.getItems().add(newItem);
        }
        return true;
    }

}
