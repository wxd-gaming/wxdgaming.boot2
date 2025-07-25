package wxdgaming.game.server.script.bag;

import wxdgaming.game.bean.goods.ItemTypeConst;

/**
 * 背包脚本
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-25 10:56
 **/
public interface IBagScript {

    default ItemTypeConst type() {
        return ItemTypeConst.NONE;
    }

}
