package wxdgaming.game.server.script.bag.cost;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.bag.BagChangesProcess;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.bag.ItemGrid;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.role.PlayerService;

import java.util.List;

/**
 * 背包变更
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 19:14
 **/
@Slf4j
@Singleton
public class CostScript {

    @Inject protected DataCenterService dataCenterService;
    @Inject protected PlayerService playerService;
    @Inject protected BagService bagService;

    public ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

    public void cost(Player player, BagChangesProcess bagChangesProcess, QItem qItem, long count) {
        BagType bagType = bagChangesProcess.getBagType();
        ItemBag itemBag = bagChangesProcess.getItemBag();
        List<ItemGrid> itemGridList = itemBag.itemGridListByCfgId(qItem.getId());
        itemGridList.sort((itemGrid1, itemGrid2) -> {
            Item o1 = itemGrid1.getItem();
            Item o2 = itemGrid2.getItem();
            if (o1.isBind() != o2.isBind()) {
                return Boolean.compare(o2.isBind(), o1.isBind());
            }
            if (o1.getExpireTime() != o2.getExpireTime()) {
                if (o1.getExpireTime() == 0) {
                    return -1;
                }
                if (o2.getExpireTime() == 0) {
                    return 1;
                }
                return Long.compare(o1.getExpireTime(), o2.getExpireTime());
            }
            return Long.compare(o1.getUid(), o2.getUid());
        });

        for (ItemGrid itemGrid : itemGridList) {
            Item item = itemGrid.getItem();
            if (item.getCfgId() == qItem.getId()) {
                long hasNum = item.getCount();
                if (hasNum <= count) {
                    log.info(
                            "背包变更：{}, {}, 道具扣除, 格子：{}, {}, {}, 从背包移除, {}",
                            player, bagType, itemGrid.getGrid(), item.toName(), item.getCount(), bagChangesProcess.getReasonArgs()
                    );
                    itemBag.remove(itemGrid);
                    bagChangesProcess.addDel(itemGrid);
                    count -= hasNum;
                } else {
                    /*正常扣除*/
                    item.setCount(hasNum - count);
                    bagChangesProcess.addChange(itemGrid);
                    log.info(
                            "背包变更：{}, {}, 道具扣除, 格子：{}, {}, {}-{}={}, {}",
                            player, bagType, itemGrid.getGrid(), item.toName(), hasNum, count, item.getCount(), bagChangesProcess.getReasonArgs()
                    );
                    count = 0;
                }
                if (count <= 0) {
                    break;
                }
            }
        }
        AssertUtil.assertTrue(count <= 0, "背包变更：%s, %s, %s, 数量不足", player, bagType, qItem.getToName());
    }

    public void cost(Player player, BagChangesProcess bagChangesProcess, ItemGrid itemGrid, long count) {
        ItemBag itemBag = bagChangesProcess.getItemBag();
        Item item = itemGrid.getItem();
        long hasNum = item.getCount();
        if (hasNum <= count) {
            log.info(
                    "背包变更：{}, {}, 道具扣除, 格子：{}, {}, {}, 从背包移除, {}",
                    player, bagChangesProcess.getBagType(), itemGrid.getGrid(), item.toName(), item.getCount(), bagChangesProcess.getBagType()
            );
            itemBag.remove(itemGrid);
            bagChangesProcess.addDel(itemGrid);
        } else {
            /*正常扣除*/
            item.setCount(hasNum - count);
            bagChangesProcess.addChange(itemGrid);
            log.info(
                    "背包变更：{}, {}, 道具扣除, 格子：{}, {}, {}-{}={}, {}",
                    player, bagChangesProcess.getBagType(), itemGrid.getGrid(), item.toName(), hasNum, count, item.getCount(), bagChangesProcess.getBagType()
            );
        }
    }

}
