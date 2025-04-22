package wxdgaming.game.test.bean.goods;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.collection.concurrent.ConcurrentTable;
import wxdgaming.boot2.starter.batis.EntityLongUID;
import wxdgaming.boot2.starter.batis.ann.DbTable;

import java.util.concurrent.ConcurrentHashMap;

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
    private ConcurrentTable<Integer, Long, Item> items = new ConcurrentTable<>(new ConcurrentHashMap<>());

}
