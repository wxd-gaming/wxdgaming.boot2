package wxdgaming.game.server.script.gm.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.basic.core.Reason;
import wxdgaming.game.basic.core.ReasonDTO;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.gm.ann.GM;
import wxdgaming.game.server.script.role.PlayerService;

/**
 * 角色等级相关
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:50
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
    public Object lv(Player player, JSONArray args) {
        int oldLv = player.getLevel();
        player.setLevel(args.getInteger(1));
        log.info("{} 当前等级:{} 设置等级为：{}", player, oldLv, player.getLevel());
        return null;
    }

    @GM
    public Object addExp(Player player, JSONArray args) {
        long exp = args.getLongValue(1);
        this.playerService.addExp(player, exp, ReasonDTO.of(Reason.GM));
        return null;
    }

}
