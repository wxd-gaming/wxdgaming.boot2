package wxdgaming.game.server.script.bag.cost.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagChangesEvent;
import wxdgaming.game.server.script.bag.cost.CostScript;

/**
 * 经验扣除
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-23 17:35
 **/
@Slf4j
@Singleton
public class ExpCostScript extends CostScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.EXP;
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent,
                               QItem qItem, long count, ReasonArgs reasonArgs) {
        long hasExp = player.getExp();
        if (hasExp < count) {
            throw new IllegalArgumentException("经验不足");
        }
        player.setExp(hasExp - count);
        log.info("扣除道具：{}, {}, 变更 {} - {} = {}, {}", player, qItem.getToName(), hasExp, count, player.getExp(), reasonArgs);
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, Item item, long count, ReasonArgs reasonArgs) {
        cost(player, bagChangesEvent, item.qItem(), count, reasonArgs);
    }

}
