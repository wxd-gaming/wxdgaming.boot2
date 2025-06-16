package wxdgaming.game.server.bean.bag;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.game.message.bag.BagType;

import java.util.HashMap;

/**
 * 背包容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 19:49
 **/
@Getter
@Setter
public class BagPack extends ObjectBase {

    /** key:背包类型, value:{key:道具id, value:道具} */
    private HashMap<BagType, ItemBag> bagMap = new HashMap<>();

    public ItemBag itemBag(BagType type) {
        return bagMap.get(type);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean bagFull() {
        return itemBag(BagType.Bag).checkFull();
    }

    /** 空闲的格子数 */
    public int bagFreeGrid() {
        return itemBag(BagType.Bag).freeGrid();
    }

}
