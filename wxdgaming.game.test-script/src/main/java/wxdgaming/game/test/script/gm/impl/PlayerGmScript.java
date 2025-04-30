package wxdgaming.game.test.script.gm.impl;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.script.gm.ann.GM;

/**
 * 角色等级相关
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:50
 **/
@Slf4j
@Singleton
public class PlayerGmScript {

    @GM
    public Object lv(Player player, String[] args) {
        player.setLevel(Integer.parseInt(args[1]));
        return null;
    }

    @GM
    public Object addExp(Player player, String[] args) {
        long exp = Long.parseLong(args[1]);

        return null;
    }

}
