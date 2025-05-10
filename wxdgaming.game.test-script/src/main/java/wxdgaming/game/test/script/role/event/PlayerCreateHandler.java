package wxdgaming.game.test.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.OnCreateRole;
import wxdgaming.game.test.script.goods.BagService;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Singleton
public class PlayerCreateHandler {

    final BagService bagService;

    @Inject
    public PlayerCreateHandler(BagService bagService) {
        this.bagService = bagService;
    }

    /** 创建角色之后赠送初始化道具 */
    @OnCreateRole
    public void onCreateRoleInitGoods(Player player) {
        log.info("角色创建:{}", player);
        ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        List<ItemCfg> rewards = new ArrayList<>();
        rewards.add(builder.cfgId(1).count(100).build());
        rewards.add(builder.cfgId(2).count(100).build());
        rewards.add(builder.cfgId(3).count(10000).build());
        rewards.add(builder.cfgId(4).count(10000).build());
        rewards.add(builder.cfgId(5).count(1).build());
        long serialNumber = System.nanoTime();
        bagService.gainItems4Cfg(player, serialNumber, rewards, "创角赠送道具:", 1001);
    }

}
