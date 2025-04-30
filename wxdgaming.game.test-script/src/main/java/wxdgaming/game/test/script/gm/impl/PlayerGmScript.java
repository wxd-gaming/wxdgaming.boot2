package wxdgaming.game.test.script.gm.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.gm.ann.GM;
import wxdgaming.game.test.script.role.PlayerService;

/**
 * 角色等级相关
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:50
 **/
@Slf4j
@Singleton
public class PlayerGmScript {

    final PlayerService playerService;

    @Inject
    public PlayerGmScript(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GM
    public Object lv(Player player, String[] args) {
        int oldLv = player.getLevel();
        player.setLevel(Integer.parseInt(args[1]));
        log.info("{} 当前等级:{} 设置等级为：{}", player, oldLv, player.getLevel());
        return null;
    }

    @GM
    public Object addExp(Player player, String[] args) {
        long exp = Long.parseLong(args[1]);
        this.playerService.addExp(player, exp, "gm命令", System.currentTimeMillis());
        return null;
    }

}
