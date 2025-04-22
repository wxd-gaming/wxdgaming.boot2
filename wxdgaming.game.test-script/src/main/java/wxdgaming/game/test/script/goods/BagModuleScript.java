package wxdgaming.game.test.script.goods;

import com.google.inject.Singleton;
import wxdgaming.boot2.core.InitPrint;
import wxdgaming.game.test.bean.goods.BagPack;
import wxdgaming.game.test.bean.role.Player;

/**
 * 背包逻辑脚本
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:39
 **/
@Singleton
public class BagModuleScript implements InitPrint {


    public long itemCount(Player player, BagPack bagPack, int itemId) {
        return 0;
    }

}
