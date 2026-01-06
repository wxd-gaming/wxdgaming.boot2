package wxdgaming.game.server.script.gm.impl;

import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.reason.ReasonConst;
import wxdgaming.game.server.bean.reason.ReasonDTO;
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
@Component
public class PlayerGmScript {

    final PlayerService playerService;

    public PlayerGmScript(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GM(group = "角色", name = "修改等级", level = 2, param = "具体数字")
    public Object lv(Player player, JSONArray args) {
        int oldLv = player.getLevel();
        player.setLevel(args.getInteger(1));
        log.info("{} 当前等级:{} 设置等级为：{}", player, oldLv, player.getLevel());
        return null;
    }

    @GM(group = "角色", name = "添加经验", level = 2, param = "具体数字, 最大long")
    public Object addExp(Player player, JSONArray args) {
        long exp = args.getLongValue(1);
        this.playerService.addExp(player, exp, ReasonDTO.of(ReasonConst.GM));
        return null;
    }

}
