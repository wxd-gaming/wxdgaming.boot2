package wxdgaming.game.server.script.bag.cost.impl.exp;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.BagChangesEvent;
import wxdgaming.game.server.bean.bag.ItemGrid;
import wxdgaming.game.server.bean.role.Player;
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

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, QItem qItem, long count) {
        long hasExp = player.getExp();
        if (hasExp < count) {
            throw new IllegalArgumentException("经验不足");
        }
        playerService.setExp(player, hasExp - count, bagChangesEvent.getReasonArgs());
        log.info("{} 当前经验：{} 扣除经验:{}, {}", player, player.getExp(), count, bagChangesEvent.getReasonArgs());
    }

    @Override public void cost(Player player, BagChangesEvent bagChangesEvent, ItemGrid itemGrid, long count) {
        cost(player, bagChangesEvent, itemGrid.getItem().qItem(), count);
    }

}
