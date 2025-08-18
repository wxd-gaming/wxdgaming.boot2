package wxdgaming.game.server.script.bag.gain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.HoldRunApplication;
import wxdgaming.boot2.starter.excel.store.DataRepository;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.cfg.QItemTable;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.bag.BagChangesContext;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.bag.ItemGrid;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.bag.IBagScript;
import wxdgaming.game.server.script.role.PlayerService;

import java.util.List;

/**
 * 获得道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 19:13
 **/
@Slf4j
@Singleton
public class GainScript extends HoldRunApplication implements IBagScript {

    @Inject protected DataCenterService dataCenterService;
    @Inject protected PlayerService playerService;
    @Inject protected BagService bagService;

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
        return itemBag.itemCountByCfgId(cfgId);
    }

    /** 将道具添加进入背包 */
    public boolean gain(BagChangesContext bagChangesContext, Item newItem) {
        Player player = bagChangesContext.getPlayer();
        BagType bagType = bagChangesContext.getBagType();
        ItemBag itemBag = bagChangesContext.getItemBag();
        ReasonDTO reasonDTO = bagChangesContext.getReasonDTO();
        long count = newItem.getCount();
        /* TODO 叠加 */
        QItem qItem = DataRepository.getIns().dataTable(QItemTable.class, newItem.getCfgId());
        int maxCount = qItem.getMaxCount();
        if (count != maxCount/*TODO入股相等说明最大值，不去执行叠加操作直接入包*/) {
            Item[] itemGrids = itemBag.getItemGrids();
            for (int i = 0; i < itemGrids.length; i++) {
                Item value = itemGrids[i];
                if (value == null) continue;
                if (value.getCfgId() != newItem.getCfgId()) continue;
                if (value.isBind() != newItem.isBind()) continue;/* TODO 绑定状态一致 */
                if (value.getExpireTime() != newItem.getExpireTime()/* TODO 过期时间一致 */) continue;
                long oldCount = value.getCount();
                if (oldCount < maxCount || maxCount == 0/*表示无限叠加*/) {
                    if (maxCount > 0 && oldCount + count > maxCount) {
                        long addChange = maxCount - oldCount;
                        count -= addChange;
                        value.setCount(maxCount);
                        log.info(
                                "背包变更：{}, {}, 道具叠加, 格子：{}, {}, {}+{}={}, {}",
                                player, bagType, i, value.toName(), oldCount, addChange, value.getCount(), reasonDTO
                        );
                    } else {
                        value.setCount(oldCount + count);
                        log.info(
                                "背包变更：{}, {}, 道具叠加, 格子：{}, {}, {}+{}={}, {}",
                                player, bagType, i, value.toName(), oldCount, count, value.getCount(), reasonDTO
                        );
                        count = 0;
                    }
                    bagChangesContext.addChange(new ItemGrid(i, value));
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
            ItemGrid itemGrid = itemBag.add(newItem);
            log.info(
                    "背包变更：{}, {}, 道具新增, 格子：{}, {}, count={}, {}",
                    player, bagType, itemGrid.getGrid(), newItem.toName(), newItem.getCount(), reasonDTO
            );
            bagChangesContext.addChange(itemGrid);
        }
        return true;
    }

}
