package wxdgaming.game.test.script.role.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.game.test.bean.StatusConst;
import wxdgaming.game.test.bean.role.Player;
import wxdgaming.game.test.event.OnLogout;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Singleton
public class PlayerLogoutEvent {

    @Inject
    public PlayerLogoutEvent() {
    }

    /** 创建角色之后赠送初始化道具 */
    @OnLogout
    public void onLogout(Player player) {
        log.info("玩家下线: {}", player.getSocketSession());
        player.setSocketSession(null);
        player.getStatus().addFlags(StatusConst.Offline);
    }

}
