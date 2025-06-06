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
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagChangesEvent;
import wxdgaming.game.server.script.bag.BagService;
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

    @SuppressWarnings("unchecked")
    protected <T extends Item> T newItem() {
        return (T) new Item();
    }

    /** 创建道具 */
    public void newItem(List<Item> items, ItemCfg itemCfg) {
        long count = itemCfg.getNum();
        do {
            Item item = newItem();
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

    protected void initItem(Item item) {}

    /** 查询指定道具背包数量 */
    public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return itemBag
                .getItems()
                .stream()
                .filter(item -> item.getCfgId() == cfgId)
                .mapToLong(Item::getCount)
                .sum();
    }

    /**
     * 将道具添加进入背包
     *
     * @return
     */
    public boolean gain(BagChangesEvent bagChangesEvent, Item newItem) {
        Player player = bagChangesEvent.getPlayer();
        BagType bagType = bagChangesEvent.getBagType();
        ItemBag itemBag = bagChangesEvent.getItemBag();
        ReasonArgs reasonArgs = bagChangesEvent.getReasonArgs();
        long count = newItem.getCount();
        /* TODO 叠加 */
        QItem qItem = DataRepository.getIns().dataTable(QItemTable.class, newItem.getCfgId());
        int maxCount = qItem.getMaxCount();
        if (count != maxCount/*TODO入股相等说明最大值，不去执行叠加操作直接入包*/) {
            for (Item value : itemBag.getItems()) {
                if (value.getCfgId() != newItem.getCfgId()) continue;
                if (value.isBind() != newItem.isBind()) continue;/* TODO 绑定状态一致 */
                if (value.getExpireTime() != newItem.getExpireTime()/* TODO 过期时间一致 */) continue;
                long oldCount = value.getCount();
                if (oldCount < maxCount || maxCount == 0/*表示无限叠加*/) {
                    if (maxCount > 0 && oldCount + count > maxCount) {
                        long addChange = maxCount - oldCount;
                        count -= addChange;
                        value.setCount(maxCount);
                        log.info("道具入包：{}, {}, 道具叠加, {}, {}+{}={}, {}", player, bagType, value.toName(), oldCount, addChange, value.getCount(), reasonArgs);
                    } else {
                        value.setCount(oldCount + count);
                        log.info("道具入包：{}, {}, 道具叠加, {}, {}+{}={}, {}", player, bagType, value.toName(), oldCount, count, value.getCount(), reasonArgs);
                        count = 0;
                    }
                    bagChangesEvent.addChange(value);
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
            log.info("道具入包：{}, {}, 道具新增, {}, count={}, {}", player, bagType, newItem.toName(), newItem.getCount(), reasonArgs);
            itemBag.getItems().add(newItem);
            bagChangesEvent.addChange(newItem);
        }
        return true;
    }

}
