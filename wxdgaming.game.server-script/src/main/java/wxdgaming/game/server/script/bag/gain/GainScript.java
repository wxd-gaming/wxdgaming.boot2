package wxdgaming.game.server.script.bag.gain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.role.PlayerService;

import java.util.HashSet;
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
            QItem qItem = DataRepository.getIns().dataTable(QItemTable.class, itemCfg.getCfgId());
            int maxCount = qItem.getMaxCount();
            if (maxCount > 0 && count > maxCount) {
                item.setCount(maxCount);
                count -= maxCount;
            } else {
                item.setCount(count);
                count = 0;
            }
            item.setBind(itemCfg.isBind());
            item.setExpireTime(itemCfg.getExpirationTime());
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
    public boolean gain(Player player, ResUpdateBagInfo resUpdateBagInfo, BagType bagType, ItemBag itemBag, Item newItem, ReasonArgs reasonArgs) {
        long count = newItem.getCount();
        HashSet<Item> changeItems = new HashSet<>();
        for (Item value : itemBag.getItems()) {
            /* TODO 叠加 */
            QItem qItem = DataRepository.getIns().dataTable(QItemTable.class, value.getCfgId());
            int maxCount = qItem.getMaxCount();
            if (value.getCfgId() != newItem.getCfgId()) continue;
            if (value.isBind() != newItem.isBind()) continue;/* TODO 绑定状态一致 */
            if (value.getExpireTime() == newItem.getExpireTime()/* TODO 过期时间一致 */) continue;
            if (value.getCount() < maxCount || maxCount == 0/*表示无限叠加*/) {
                if (maxCount > 0 && value.getCount() + count > maxCount) {
                    count = maxCount - value.getCount();
                    value.setCount(maxCount);
                } else {
                    count = 0;
                    value.setCount(value.getCount() + count);
                }
                changeItems.add(value);
            }
            if (count < 1)
                break;
        }
        if (!changeItems.isEmpty()) {
            for (Item changeItem : changeItems) {
                resUpdateBagInfo.getItems().add(changeItem.toItemBean());
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
            resUpdateBagInfo.getItems().add(newItem.toItemBean());
        }
        return true;
    }

}
