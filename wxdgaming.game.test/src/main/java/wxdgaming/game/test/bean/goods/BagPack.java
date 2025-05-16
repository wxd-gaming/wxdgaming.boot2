package wxdgaming.game.test.bean.goods;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;
import wxdgaming.boot2.starter.batis.ColumnType;
import wxdgaming.boot2.starter.batis.ann.DbColumn;

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
    @DbColumn(length = Integer.MAX_VALUE, columnType = ColumnType.String)
    private HashMap<Integer, ItemBag> items = new HashMap<>();

    @JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public boolean isFull() {
        ItemBag itemBag = items.get(1);
        return itemBag.checkFull();
    }

    public int freeGrid() {
        ItemBag itemBag = items.get(1);
        return itemBag.freeGrid();
    }

}
