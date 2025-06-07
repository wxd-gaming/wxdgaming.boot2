package wxdgaming.game.server.bean.bag;

import lombok.AccessLevel;
import lombok.Getter;
import wxdgaming.boot2.core.util.AssertUtil;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.server.bean.role.Player;

import java.util.HashSet;

/**
 * 扣除参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-05 19:20
 **/
@Getter
public class BagChangesEvent {

    final Player player;
    final BagType bagType;
    final ItemBag itemBag;
    final ReasonArgs reasonArgs;
    @Getter(AccessLevel.PRIVATE)
    final ResUpdateBagInfo resUpdateBagInfo;
    final HashSet<ItemGrid> changeItems = new HashSet<>();

    public BagChangesEvent(Player player, BagType bagType, ItemBag itemBag, ReasonArgs reasonArgs) {
        this.player = player;
        this.bagType = bagType;
        this.itemBag = itemBag;
        this.reasonArgs = reasonArgs;
        resUpdateBagInfo = new ResUpdateBagInfo();
        resUpdateBagInfo.setBagType(bagType);
        resUpdateBagInfo.setReason(reasonArgs.getReason().name());
    }

    /** 添加货币 */
    public void addCurrency(int cfgId, long num) {
        long merged = itemBag.getCurrencyMap().merge(cfgId, num, Math::addExact);
        resUpdateBagInfo.getCurrencyMap().put(cfgId, merged);
    }

    /** 扣除货币 */
    public void subtractCurrency(int cfgId, long num) {
        long merged = itemBag.getCurrencyMap().merge(cfgId, num, Math::subtractExact);
        resUpdateBagInfo.getCurrencyMap().put(cfgId, merged);
    }

    public void addDel(ItemGrid itemGrid) {
        changeItems.remove(itemGrid);
        resUpdateBagInfo.getDelItemIds().add(itemGrid.getGrid());
    }

    public void addChange(ItemGrid item) {
        AssertUtil.assertNull(item, "null");
        changeItems.add(item);
    }

    public ResUpdateBagInfo toResUpdateBagInfo() {
        for (ItemGrid changeItem : changeItems) {
            resUpdateBagInfo.getChangeItems().put(changeItem.getGrid(), changeItem.getItem().toItemBean());
        }
        return resUpdateBagInfo;
    }

}
