package wxdgaming.game.server.script.bag;

import lombok.Getter;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.bag.BagType;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.server.bean.goods.Item;
import wxdgaming.game.server.bean.goods.ItemBag;
import wxdgaming.game.server.bean.role.Player;

import java.util.HashSet;

/**
 * 扣除参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-05 19:20
 **/
@Getter
public class BagChanges {

    final Player player;
    final BagType bagType;
    final ItemBag itemBag;
    final ReasonArgs reasonArgs;
    final ResUpdateBagInfo resUpdateBagInfo;
    final HashSet<Item> changeItems = new HashSet<>();

    public BagChanges(Player player, BagType bagType, ItemBag itemBag, ReasonArgs reasonArgs) {
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

    public void addDel(Item item) {
        changeItems.remove(item);
        resUpdateBagInfo.getDelItemIds().add(item.getUid());
    }

    public void addChange(Item item) {
        changeItems.add(item);
    }

    public ResUpdateBagInfo build() {
        for (Item changeItem : changeItems) {
            resUpdateBagInfo.getItems().add(changeItem.toItemBean());
        }
        return resUpdateBagInfo;
    }

}
