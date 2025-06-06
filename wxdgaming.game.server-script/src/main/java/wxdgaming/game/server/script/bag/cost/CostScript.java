package wxdgaming.game.server.script.bag.cost;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagChanges;

import java.util.List;

/**
 * 扣除道具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 19:14
 **/
@Slf4j
@Singleton
public class CostScript {

    public ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

    public void cost(Player player, BagChanges bagChanges, QItem qItem, long count, ReasonArgs reasonArgs) {
        BagType bagType = bagChanges.getBagType();
        ItemBag itemBag = bagChanges.getItemBag();
        List<Item> list = itemBag.getItems().stream()
                .filter(v -> v.getCfgId() == qItem.getId())
                .sorted((o1, o2) -> {
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
                })
                .toList();

        for (Item item : list) {
            if (item.getCfgId() == qItem.getId()) {
                long hasNum = item.getCount();
                if (hasNum <= count) {
                    log.info("扣除道具：{}, {}, {}, 从背包移除, {}", player, bagType, item.toName(), reasonArgs);
                    bagChanges.addDel(item);
                    count -= hasNum;
                } else {
                    /*正常扣除*/
                    item.setCount(hasNum - count);
                    bagChanges.addChange(item);
                    log.info("扣除道具：{}, {}, {}, 变更 {} - {} = {}, {}", player, bagType, item.toName(), hasNum, count, item.getCount(), reasonArgs);
                    count = 0;
                }
                if (count <= 0) {
                    break;
                }
            }
        }
        AssertUtil.assertTrue(count <= 0, "扣除道具：%s, %s, %s, 数量不足", player, bagType, qItem.getToName());
    }

    public void cost(Player player, BagChanges bagChanges, Item item, long count, ReasonArgs reasonArgs) {
        long hasNum = item.getCount();
        if (hasNum <= count) {
            log.info("扣除道具：{}, {}, {}, 从背包移除, {}", player, bagChanges.getBagType(), item.toName(), reasonArgs);
            bagChanges.addDel(item);
        } else {
            /*正常扣除*/
            item.setCount(hasNum - count);
            bagChanges.addChange(item);
            log.info("扣除道具：{}, {}, {}, 变更 {} - {} = {}, {}", player, bagChanges.getBagType(), item.toName(), hasNum, count, item.getCount(), reasonArgs);
        }
    }

}
