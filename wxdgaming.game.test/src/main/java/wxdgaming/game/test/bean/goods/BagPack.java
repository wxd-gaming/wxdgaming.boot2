package wxdgaming.game.test.bean.goods;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbColumn;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.util.HashMap;

/**
 * 背包容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-21 19:49
 **/
@Getter
@Setter
@DbTable
public class BagPack extends EntityLongUID {

    /** key:背包类型, value:{key:道具id, value:道具} */
    @DbColumn(length = Integer.MAX_VALUE)
    private HashMap<Integer, ItemBag> items = new HashMap<>();

    public boolean isFull() {
        ItemBag itemBag = items.get(1);
        return itemBag.isFull();
    }

    public int freeGrid() {
        ItemBag itemBag = items.get(1);
        return itemBag.freeGrid();
    }

}
