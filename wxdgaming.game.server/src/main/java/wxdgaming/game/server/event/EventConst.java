package wxdgaming.game.server.event;

import wxdgaming.boot2.starter.event.Event;
import wxdgaming.game.server.bean.role.Player;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-09-04 11:35
 **/
public interface EventConst {

    public record LevelUpEvent(Player player, int changeLv) implements Event {
    }

}
