package wxdgaming.game.test.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.goods.ItemCfg;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.event.OnCreateRole;
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
public class PlayerCreateEvent {

    final BagService bagService;

    @Inject
    public PlayerCreateEvent(BagService bagService) {
        this.bagService = bagService;
    }

    /** 创建角色之后赠送初始化道具 */
    @OnCreateRole
    public void onCreateRoleInitGoods(Player player) {
        log.info("角色创建:{}", player);
        ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        List<ItemCfg> rewards = new ArrayList<>();
        rewards.add(builder.cfgId(10001).count(100).build());
        rewards.add(builder.cfgId(30001).count(100).build());
        long serialNumber = System.nanoTime();
        bagService.gainItems4Cfg(player, serialNumber, rewards, "赠送初始化道具:", 1001);
    }

}
